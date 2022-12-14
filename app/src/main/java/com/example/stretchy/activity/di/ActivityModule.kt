package com.example.stretchy.activity.di

import androidx.activity.ComponentActivity
import com.example.stretchy.activity.di.scope.ActivityScope
import com.example.stretchy.features.executetraining.Speaker
import dagger.Module
import dagger.Provides

@Module
class ActivityModule {

    @ActivityScope
    @Provides
    fun provideSpeaker(activity: ComponentActivity) =
        Speaker(context = activity.baseContext)
}