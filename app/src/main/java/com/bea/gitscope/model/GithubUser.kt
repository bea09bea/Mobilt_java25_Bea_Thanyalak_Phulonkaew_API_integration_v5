package com.bea.gitscope.model

data class GitHubUser(
    val login: String,
    val name: String?,
    val avatar_url: String,
    val bio: String?,
    val public_repos: Int,
    val followers: Int,
    val updated_at: String,

    //Most popular project:
    //Most used language:

    //hjärta icon
    val id: Long = 0,
    var isFavorite: Boolean = false

)
