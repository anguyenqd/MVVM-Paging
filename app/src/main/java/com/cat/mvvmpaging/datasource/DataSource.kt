package com.cat.mvvmpaging.datasource

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.cat.domain.entity.State
import com.cat.framework.local.database.RedditDatabase
import com.cat.mvvmpaging.datasource.network.RedditApi
import com.cat.mvvmpaging.datasource.entities.LoadPostRequest
import com.cat.mvvmpaging.datasource.entities.PagingPosts
import com.cat.mvvmpaging.datasource.entities.Post
import com.cat.mvvmpaging.datasource.network.PostBoundaryCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class DataSource : KoinComponent {
    private val database: RedditDatabase by inject()
    private val redditAPI: RedditApi by inject()

    fun loadPostBySubName(loadPostRequest: LoadPostRequest): PagingPosts {
        val boundaryCallback: PostBoundaryCallback = PostBoundaryCallback(
            subName = loadPostRequest.subName,
            coroutineContext = loadPostRequest.coroutine,
            networkPageSize = PAGE_SIZE,
            handleResponse = this::insertToLocalDatabase,
            onLoadMore = this::loadMorePosts,
            onZeroLoad = this::loadFromZero
        )

        val refreshTrigger: MutableLiveData<Unit> = MutableLiveData<Unit>()
        val refreshStateLiveData: LiveData<State> = Transformations.switchMap(refreshTrigger) {
            this@DataSource.getPostBySubName(loadPostRequest)
        }

        val postLiveData: LiveData<PagedList<Post>> = LivePagedListBuilder(
            this.database
                .redditPostDao()
                .getRedditPostsBySub(loadPostRequest.subName),
                PagedList.Config
                    .Builder()
                    .setEnablePlaceholders(false)
                    .setInitialLoadSizeHint(PAGE_SIZE)
                    .setPageSize(PAGE_SIZE)
                    .setPrefetchDistance(15)
                    .build()
        ).setBoundaryCallback(boundaryCallback).build()

        // Building Paging DataSource
        return PagingPosts(
            data = postLiveData,
            loadMoreState = boundaryCallback.loadMoreState,
            refresh = {
                // Trigger the refresh live data
                refreshTrigger.value = null
            },
            refreshState = refreshStateLiveData
        )
    }

    private suspend fun loadFromZero(subName: String, loadSize: Int) =
        redditAPI.getTop(subreddit = subName, limit = loadSize)
            .data
            .children
            .map {
                it.data
            }

    private suspend fun loadMorePosts(
        subName: String,
        after: String,
        loadSize: Int
    ) = redditAPI.getTopAfter(subreddit = subName, after = after, limit = loadSize)
        .data
        .children
        .map {
            it.data
        }

    private fun getPostBySubName(loadPostRequest: LoadPostRequest): LiveData<State> {
        // This LiveData emit states of get post by sub name process
        val getPostBySubNameState: MutableLiveData<State> = MutableLiveData()
        // launch coroutine blocking
        CoroutineScope(loadPostRequest.coroutine).launch {
            getPostBySubNameState.postValue(State.LOADING)
            // TODO get more accurate error
            val newPosts: List<Post>? =
                loadFromZero(
                    loadPostRequest.subName,
                    PAGE_SIZE
                )

            if (newPosts == null) {
                getPostBySubNameState.postValue(State.error("Refreshing reddit posts by subname ${loadPostRequest.subName} failed!"))
                return@launch
            }

            if (newPosts.isEmpty()) {
                getPostBySubNameState.postValue(State.EMPTY)
                return@launch
            }

            // 1st, Delete all posts belong to the requested sub name
            this@DataSource.deletePostBySubNameInLocalDatabase(loadPostRequest.subName)
            // 2nd, insert the new post with the requested sub name
            this@DataSource.insertToLocalDatabase(loadPostRequest.subName, newPosts)
            getPostBySubNameState.postValue(State.LOADED)
        }

        return getPostBySubNameState
    }

    private fun deletePostBySubNameInLocalDatabase(subName: String) {
        this.database.redditPostDao().deleteRedditPostsBySub(subName = subName)
    }

    private fun insertToLocalDatabase(subName: String, newPosts: List<Post>) {
        this.database.redditPostDao().addRedditPosts(newPosts.map { post ->
            post.indexInResponse =
                this.database.redditPostDao().getNextIndexInSub(subName = subName)
            post
        })
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}