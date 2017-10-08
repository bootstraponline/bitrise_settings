import retrofit2.Call
import retrofit2.http.GET

class Api {
    interface ApiService {
        @GET("me/apps")
        fun getApps(): Call<AppData>
    }

    companion object {
        private val apiService: ApiService by lazy {
            RestAdapter.retrofit.create(ApiService::class.java)
        }

        fun getAppsForOrg(orgName: String): List<AppObject> {
            val appData = apiService.getApps().execute()

            return appData.body().data
                    .filter { orgName == it.repo_owner }
        }
    }
}
