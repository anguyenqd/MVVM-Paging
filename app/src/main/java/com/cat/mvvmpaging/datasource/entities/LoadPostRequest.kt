package com.cat.mvvmpaging.datasource.entities

import kotlin.coroutines.CoroutineContext

data class LoadPostRequest(val subName : String, val coroutine : CoroutineContext)