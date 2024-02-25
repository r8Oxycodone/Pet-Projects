package com.example.aptitudetest.di

import android.app.Application

class App : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = AppComponentImpl(this)
    }
}