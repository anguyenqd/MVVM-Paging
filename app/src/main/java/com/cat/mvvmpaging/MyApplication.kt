package com.cat.mvvmpaging

import android.app.Application
import com.cat.mvvmpaging.service_locator.frameworkModules
import com.cat.mvvmpaging.service_locator.viewModelModules
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.stetho.Stetho
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(arrayListOf(viewModelModules, frameworkModules))
        }

        Fresco.initialize(this)
        Stetho.initializeWithDefaults(this);
    }
}