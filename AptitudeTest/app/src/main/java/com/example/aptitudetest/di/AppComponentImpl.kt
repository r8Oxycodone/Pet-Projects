package com.example.aptitudetest.di

import android.content.Context
import com.example.aptitudetest.presentation.fragments.LaunchesDetailsFragment
import com.example.aptitudetest.presentation.fragments.LaunchesFragment

class AppComponentImpl(private val context: Context) : AppComponent {

    private val dataModule = DataModule()

    override fun inject(launchesFragment: LaunchesFragment) {
        launchesFragment.imageLoader = dataModule.provideImageLoader(context)
        launchesFragment.spaceXDataSource = dataModule.provideSpaceXDataSource(
            dataModule.provideMoshi(),
            dataModule.provideOkHttpClient()
        )
    }

    override fun inject(launchesDetailsFragment: LaunchesDetailsFragment) {
        launchesDetailsFragment.imageLoader = dataModule.provideImageLoader(context)
        launchesDetailsFragment.spaceXDataSource = dataModule.provideSpaceXDataSource(
            dataModule.provideMoshi(),
            dataModule.provideOkHttpClient()
        )
        launchesDetailsFragment.crewRepository = dataModule.provideCrewRepository()
    }
}