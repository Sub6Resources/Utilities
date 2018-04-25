package com.sub6resources.utilities

import android.app.Application
import org.koin.android.ext.android.startKoin
import org.koin.dsl.module.Module

open class BaseApplication(vararg val modules: Module): Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPrefs.sharedPreferences = sharedPreferences
        startKoin(this, modules.toList())
    }
}