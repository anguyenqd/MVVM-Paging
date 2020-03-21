package com.cat.mvvmpaging.datasource.network.entities

data class ListingData(
    val children: List<RedditChildrenResponse>,
    val after: String?,
    val before: String?
)
