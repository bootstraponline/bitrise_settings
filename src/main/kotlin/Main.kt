import com.codeborne.selenide.Condition
import com.codeborne.selenide.Condition.exist
import com.codeborne.selenide.Selenide
import com.codeborne.selenide.Selenide.*
import com.codeborne.selenide.SelenideElement
import org.openqa.selenium.By

// use default 5 second timeout on waitUntil via extension method
fun SelenideElement.waitUntil(condition: Condition): SelenideElement {
    return waitUntil(condition, 30000)
}

fun StringBuilder.println(message:String) : StringBuilder {
    this.append(message + "\n")
    return this
}

object Main {

    fun getEnv(name: String): String {
        return System.getenv(name) ?: throw RuntimeException("$name is null")
    }

    fun find(cssSelector: String): SelenideElement {
        println("$(\"$cssSelector\")")
        return getElement(By.cssSelector(cssSelector))
    }

    fun logIn() {
        open("https://www.bitrise.io/users/sign_in");
        val user = getEnv("BITRISE_USER")
        val pass = getEnv("BITRISE_PASS")

        val loginInput = "input[ng-model='simpleSignInFormData.login']"
        val passwordInput = "input[ng-model='simpleSignInFormData.password']"
        val submit = "form[name='simpleSignInForm'] > input[type='submit']"

        find(loginInput).value = user
        find(passwordInput).value = pass
        find(submit).click()
    }

    fun goToDashboard() {
        val dashboard = "a[href='/apps/add']"
        find(dashboard).waitUntil(exist)
    }

    init {
        System.setProperty("selenide.browser", "Chrome");
    }

    fun click(element: SelenideElement) {
        return Selenide.executeJavaScript("arguments[0].click()", element.toWebElement()) ?: Unit
    }

    fun isAngularInputEnabled(selector: String) : Boolean {
        val input = find(selector).waitUntil(exist)
        val enabled = input.attr("class").contains("ng-not-empty")
        return enabled
    }

    // missing app idle sync from protractor so we have to try a few times in a loop until the event registers.
    fun enableAngularInput(selector: String) {
        val enabled = isAngularInputEnabled(selector)

        val tryCount = 10
        var attempts = 0
        while (!enabled) {
            attempts += 1
            if (attempts > tryCount) throw RuntimeException("Failed to enable input after ${tryCount}x trys")

            click(find(selector))
            if (find(selector + "[class~='ng-not-empty']").exists()) break

            sleep(500)
        }
    }

    fun disableAngularInput(selector: String) {
        val enabled = isAngularInputEnabled(selector)

        val tryCount = 10
        var attempts = 0
        while (enabled) {
            attempts += 1
            if (attempts > tryCount) throw RuntimeException("Failed to disable input after ${tryCount}x trys")

            click(find(selector))
            if (find(selector + "[class~='ng-empty']").exists()) break

            sleep(500)
        }
    }

    val report:StringBuilder = StringBuilder()

    fun settingsUrl(app: AppObject) : String {
        return "https://www.bitrise.io/app/" + app.slug + "#/settings"
    }

    fun setAppSettings(app: AppObject) {
        open(settingsUrl(app))

        val enableRollingBuilds = "input[ng-model='rollingBuildsAddonCtrl.addonEnabledGetterSetter']"
        enableAngularInput(enableRollingBuilds);

        // Pull Requests. Cancel previous builds for pull requests
        // Pushes. Cancel previous builds for pushes.
        val checkboxModel = "div[ng-if='rollingBuildsAddonCtrl.isAddonEnabled'] > div:nth-of-type"
        val pullRequests = checkboxModel + "(1) input"
        val pushes = checkboxModel + "(2) input"

        enableAngularInput(pullRequests)
        disableAngularInput(pushes)

        report.println("${app.title} - ${settingsUrl(app)}")
        report.println("rolling builds? " + isAngularInputEnabled(enableRollingBuilds))
        report.println("           PRs? " + isAngularInputEnabled(pullRequests))
        report.println("        pushes? " + isAngularInputEnabled(pushes))
        report.println("")
    }

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        logIn()

        goToDashboard()

        var count = 0;
        for (app in Api.getAppsForOrg(getEnv("BITRISE_ORG"))) {
            count += 1
            report.append("[$count] ")
            setAppSettings(app)
        }

        println(report.toString())
    }
}
