import com.codeborne.selenide.Condition
import com.codeborne.selenide.Condition.exist
import com.codeborne.selenide.Condition.visible
import com.codeborne.selenide.Selenide.getElement
import com.codeborne.selenide.Selenide.open
import com.codeborne.selenide.SelenideElement
import org.openqa.selenium.By

// use default 5 second timeout on waitUntil via extension method
fun SelenideElement.waitUntil(condition: Condition): SelenideElement {
    return waitUntil(condition, 5000)
}

object Main {

    fun getEnv(name: String): String {
        return System.getenv(name) ?: throw RuntimeException("$name is null")
    }

    fun find(cssSelector: String): SelenideElement {
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


    fun enableAngularInput(selector:String) {
        val input = find(selector).waitUntil(exist)
        val enabled = input.attr("class").contains("ng-not-empty");

        if (!enabled) {
            input.click();
            find(selector + "[class~='ng-not-empty']").waitUntil(exist)
        }
    }

    fun disableAngularInput(selector:String) {
        val input = find(selector).waitUntil(exist)
        val enabled = input.attr("class").contains("ng-not-empty");

        if (!enabled) {
            input.click();
            find(selector + "[class~='ng-empty']").waitUntil(exist)
        }
    }

    fun setAppSettings(app:AppObject) {
       open("https://www.bitrise.io/app/" + app.slug + "#/settings")

        val enableRollingBuilds = "input[ng-model='rollingBuildsAddonCtrl.addonEnabledGetterSetter']"
        enableAngularInput(enableRollingBuilds);

        // Pull Requests. Cancel previous builds for pull requests
        // Pushes. Cancel previous builds for pushes.
        val checkboxModel = "div[ng-if='rollingBuildsAddonCtrl.isAddonEnabled'] > div:nth-of-type"
        val pullRequests = checkboxModel + "(1) input"
        val pushes = checkboxModel + "(2) input"

        enableAngularInput(pullRequests);
        disableAngularInput(pushes)
    }

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        logIn()

        goToDashboard()

        for (app in Api.getAppsForOrg("instructure")) {
            setAppSettings(app)
        }
    }
}
