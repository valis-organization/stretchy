package com.example.stretchy.app.di

import android.app.Activity
import com.example.stretchy.activity.MainActivity
import com.example.stretchy.activity.di.AppComponent
import com.example.stretchy.activity.di.scope.ActivityScope
import com.example.stretchy.activity.di.AppComponent.Companion.appComponent
import dagger.BindsInstance
import dagger.Component


@ActivityScope
@Component(
    dependencies = [AppComponent::class]
)
interface ActivityComponent {
    @Component.Factory
    interface Factory {
        fun create(
            appComponent: AppComponent,
            @BindsInstance activity: Activity
        ): ActivityComponent
    }

    fun inject(activity: MainActivity)

    companion object {
        fun create(activity: MainActivity): ActivityComponent =
            DaggerActivityComponent.factory().create(activity.appComponent, activity).apply {
                inject(activity)
            }
    }
}