package com.bea.gitscope.model


data class GitHubSearchResponse(
    val total_count: Int,
    val items: List<GitHubUser>
)