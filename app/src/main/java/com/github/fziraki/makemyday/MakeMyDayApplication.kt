package com.github.fziraki.makemyday

import android.app.Application
import com.github.fziraki.makemyday.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MakeMyDayApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MakeMyDayApplication)
            modules(appModule)
        }
    }
}