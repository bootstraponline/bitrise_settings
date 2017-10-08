import Main.getEnv
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RestAdapter {
    val baseUrl = "https://api.bitrise.io/v0.1/"
    val bitriseToken = getEnv("BITRISE_TOKEN")

    private val httpClient: OkHttpClient by lazy {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY

        OkHttpClient.Builder().addInterceptor { chain ->
            var request = chain.request()
            val builder = request.newBuilder()

            builder.addHeader("Authorization", "token $bitriseToken")

            request = builder.build()

            chain.proceed(request)
        }
        .addInterceptor(logger)
        .build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

}
