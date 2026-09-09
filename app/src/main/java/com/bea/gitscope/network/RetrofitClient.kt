package com.bea.gitscope.network
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.bea.gitscope.BuildConfig

class RetrofitClient {
}

object GitHubClient {

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->

            val request = chain.request()
                .newBuilder()
                .addHeader(
                    "Authorization",
                    "Bearer ${BuildConfig.GITHUB_TOKEN}"
                )
                .build()

            chain.proceed(request)
        }
        .build()

    val api: GitHubApi = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GitHubApi::class.java)

}