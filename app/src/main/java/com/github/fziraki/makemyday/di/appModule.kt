package com.github.fziraki.makemyday.di

import com.github.fziraki.daykit.DayKitClient
import com.github.fziraki.makemyday.AppPreferences
import com.github.fziraki.makemyday.myday.MyDayViewModel
import com.github.fziraki.makemyday.onboarding.SetupPageViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single {
        AppPreferences(get())
    }
    single {
        DayKitClient.Builder(androidContext())
            .setLocation(52.5, 5.47)
            .build()
    }

    viewModel {
        MyDayViewModel(
            client = get(),
            preferences = get()
        )
    }

    viewModel {
        SetupPageViewModel(
            preferences = get()
        )
    }
}