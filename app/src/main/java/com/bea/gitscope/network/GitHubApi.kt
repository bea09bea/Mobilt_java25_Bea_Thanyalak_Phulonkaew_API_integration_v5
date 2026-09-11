package com.bea.gitscope.network

import com.bea.gitscope.model.GitHubSearchResponse
import com.bea.gitscope.model.GitHubUser
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApi {
    @Headers(
        "Accept: application/vnd.github+json",
        "X-GitHub-Api-Version: 2026-03-10"
    )

    //söker efter Github användare
    @GET("search/users")
    fun searchUsers(
        @Query("q") query: String
    ): Call<GitHubSearchResponse>

    @Headers(
        "Accept: application/vnd.github+json",
        "X-GitHub-Api-Version: 2026-03-10"
    )

    //Hämta info om specifik github avnändare
    @GET("users/{username}")
    fun getUser(
        @Path("username") username: String
    ): Call<GitHubUser>
}