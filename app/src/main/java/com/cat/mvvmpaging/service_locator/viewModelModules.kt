package com.cat.mvvmpaging.service_locator

import com.cat.framework.local.database.RedditDatabase
import com.cat.mvvmpaging.datasource.network.RedditApi
import com.cat.mvvmpaging.datasource.DataSource
import com.cat.mvvmpaging.ui.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModules = module {
    viewModel { MainViewModel() }
}

val frameworkModules = module {
    single { DataSource() }
    single { RedditDatabase.create(get()) }
    single { RedditApi.create() }
}