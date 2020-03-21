package com.cat.mvvmpaging.datasource.local.database

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cat.mvvmpaging.datasource.entities.Post

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addRedditPosts(posts: List<Post>)

    @Query("SELECT * FROM post WHERE subName = :subName ORDER BY indexInResponse")
    fun getRedditPostsBySub(subName : String) : DataSource.Factory<Int, Post>

    @Query("DELETE FROM post WHERE subName = :subName")
    fun deleteRedditPostsBySub(subName: String)

    @Query("SELECT MAX(indexInResponse) + 1 FROM post WHERE subName = :subName")
    fun getNextIndexInSub(subName: String) : Int
}