package com.cat.mvvmpaging.ui.main

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.cat.domain.entity.State
import com.cat.mvvmpaging.datasource.DataSource
import com.cat.mvvmpaging.datasource.entities.Post
import com.cat.mvvmpaging.datasource.entities.LoadPostRequest
import com.cat.mvvmpaging.datasource.entities.PagingPosts
import com.cat.mvvmpaging.util.Utils
import kotlinx.coroutines.Dispatchers
import org.koin.core.KoinComponent
import org.koin.core.inject

class MainViewModel : ViewModel(), KoinComponent {
    private val dataSource: DataSource by inject()

    val subNameTriggerLiveData: MutableLiveData<String> = MutableLiveData()
    private val getPostLiveData: LiveData<PagingPosts> =
        Transformations.map(subNameTriggerLiveData) { subName ->
            this@MainViewModel.dataSource.loadPostBySubName(
                LoadPostRequest(subName = subName, coroutine = Dispatchers.IO)
            )
        }

    val postsLiveData: LiveData<PagedList<Post>> =
        Transformations.switchMap(getPostLiveData) {
            it?.data
        }

    val refreshState: LiveData<State> =
        Transformations.switchMap(getPostLiveData) {
            it?.refreshState
        }

    val loadMoreState: LiveData<State> =
        Transformations.switchMap(getPostLiveData) {
            it?.loadMoreState
        }

    fun onLoad() {
        this.subNameTriggerLiveData.value = Utils.currentSubName()
    }

    fun refresh(subName: String) {
        // Save requesting subname for next time opening of the app
        Utils.setSubName(subName = subName)
        if (subName == subNameTriggerLiveData.value) {
            getPostLiveData.value?.refresh?.invoke()
        } else {
            subNameTriggerLiveData.value = subName
        }
    }
}
