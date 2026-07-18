package com.github.fziraki.makemyday.di

import com.github.fziraki.daykit.DayKitClient
import com.github.fziraki.makemyday.AppPreferences
import com.github.fziraki.makemyday.data.PreferencesRepository
import com.github.fziraki.makemyday.locationsearch.LocationSearchViewModel
import com.github.fziraki.makemyday.myday.AudioPlayer
import com.github.fziraki.makemyday.myday.ExoAudioPlayer
import com.github.fziraki.makemyday.myday.MyDayViewModel
import com.github.fziraki.makemyday.onboarding.SetupPageViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<PreferencesRepository> {
        AppPreferences(get())
    }

    single<DayKitClient> {
        DayKitClient.Builder(androidContext()).build()
    }

    single<AudioPlayer> {
        ExoAudioPlayer(androidContext())
    }

    viewModel {
        MyDayViewModel(
            client = get(),
            preferences = get(),
            audioPlayer = get()
        )
    }

    viewModel {
        SetupPageViewModel(
            preferences = get()
        )
    }

    viewModel {
        LocationSearchViewModel(
            client = get(),
            preferences = get(),
        )
    }


}