package com.sub6resources.utilities

import android.app.Application
import android.arch.lifecycle.ViewModelStore
import android.arch.lifecycle.ViewModelStoreOwner
import android.content.Intent
import org.koin.android.ext.android.startKoin
import org.koin.dsl.module.Module

open class BaseApplication(vararg val modules: Module): Application(), ViewModelStoreOwner {

    var savedCallbacks = ArrayList<(resultCode: Int, data: Intent?) -> Unit>()

    override fun onCreate() {
        super.onCreate()
        SharedPrefs.sharedPreferences = sharedPreferences
        startKoin(this, modules.toList())
    }

    private val appViewModelStore: ViewModelStore by lazy {
        ViewModelStore()
    }

    override fun getViewModelStore(): ViewModelStore {
        return appViewModelStore
    }
}