package com.stenopolz.coffeenotes

import android.app.Application
import di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent

class MainApplication: Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MainApplication)
        }
    }

}
