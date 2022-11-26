package com.example.stretchy.app

import android.app.Application
import com.example.stretchy.app.di.AppComponent

class StretchyApp : Application() {

    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = AppComponent.create(this)
    }

    fun getAppComponent(): AppComponent {
        return appComponent
    }
}
