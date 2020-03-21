package com.cat.mvvmpaging.datasource.network

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.cat.domain.entity.State
import com.cat.mvvmpaging.datasource.entities.Post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class PostBoundaryCallback(
    val subName: String,
    val coroutineContext: CoroutineContext,
    val networkPageSize: Int,
    val handleResponse: (String, List<Post>) -> Unit,
    val onZeroLoad: suspend (String, Int) -> List<Post>?,
    val onLoadMore: suspend (String, String, Int) -> List<Post>?
) : PagedList.BoundaryCallback<Post>() {
    val loadMoreState: MutableLiveData<State> = MutableLiveData()

    override fun onZeroItemsLoaded() {
        CoroutineScope(this.coroutineContext).launch {
            this@PostBoundaryCallback.loadMoreState.postValue(State.LOADING)
            val newPost: List<Post>? = this@PostBoundaryCallback.onZeroLoad(
                subName,
                networkPageSize
            )

            if (newPost == null) {
                this@PostBoundaryCallback
                    .loadMoreState
                    .postValue(State.error("Refreshing reddit posts by subname ${this@PostBoundaryCallback.subName} failed!"))
                return@launch
            }

            if (newPost.isEmpty()) {
                this@PostBoundaryCallback.loadMoreState.postValue(State.EMPTY)
                return@launch
            }

            this@PostBoundaryCallback.handleResponse(subName, newPost)
            this@PostBoundaryCallback.loadMoreState.postValue(State.LOADED)
        }
    }

    override fun onItemAtEndLoaded(itemAtEnd: Post) {
        CoroutineScope(this.coroutineContext).launch {
            this@PostBoundaryCallback.loadMoreState.postValue(State.LOADING)
            val morePosts: List<Post>? = this@PostBoundaryCallback.onLoadMore(
                subName,
                itemAtEnd.name,
                networkPageSize
            )

            if (morePosts == null) {
                this@PostBoundaryCallback
                    .loadMoreState
                    .postValue(State.error("Loading more reddit posts by subname ${this@PostBoundaryCallback.subName} failed!"))
                return@launch
            }

            if (morePosts.isEmpty()) {
                this@PostBoundaryCallback.loadMoreState.postValue(State.EMPTY)
                return@launch
            }

            this@PostBoundaryCallback.handleResponse(subName, morePosts)
            this@PostBoundaryCallback.loadMoreState.postValue(State.LOADED)
        }
    }
}