package com.example.aptitudetest.di

import android.content.Context
import com.example.aptitudetest.presentation.fragments.LaunchesDetailsFragment
import com.example.aptitudetest.presentation.fragments.LaunchesFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [DataModule::class])
@Singleton
interface AppComponent {
    fun inject(launchesFragment: LaunchesFragment)
    fun inject(launchesDetailsFragment: LaunchesDetailsFragment)

    @Component.Factory
    interface AppComponentFactory {
        fun create(@BindsInstance context: Context): AppComponent
    }

}