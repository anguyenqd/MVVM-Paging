package com.cat.mvvmpaging.util

import android.content.Context
import org.koin.core.KoinComponent
import org.koin.core.inject

object Utils : KoinComponent {
    private val context: Context by inject()
    private const val DEFAULT_SUB_NAME = "gaming"
    private const val PRE_NAME = "REDDIT_HEX"
    private const val PRE_SUB_NAME_NAME = "SUB_NAME"

    fun currentSubName(): String {
        val sharedPref = this.context.getSharedPreferences(PRE_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(PRE_SUB_NAME_NAME, DEFAULT_SUB_NAME) ?: DEFAULT_SUB_NAME
    }

    fun setSubName(subName: String) {
        val sharedPref = this.context.getSharedPreferences(PRE_NAME, Context.MODE_PRIVATE)
        sharedPref.edit()?.putString(PRE_SUB_NAME_NAME, subName)?.apply()
    }
}