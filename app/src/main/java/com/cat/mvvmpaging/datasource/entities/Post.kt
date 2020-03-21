package com.cat.mvvmpaging.datasource.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "post")
data class Post(
    @PrimaryKey
    val name: String,
    val title: String,
    val score: Int,
    val author: String,
    @SerializedName("subreddit")
    val subName: String,
    val num_comments: Int,
    val created: Long,
    val thumbnail: String?,
    val url: String?
) {
    // to be consistent w/ changing backend order, we need to keep a data like this
    var indexInResponse: Int = -1
}