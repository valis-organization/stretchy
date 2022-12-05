package com.example.stretchy.features.traininglist.di

import com.example.stretchy.activity.di.ActivityComponent
import com.example.stretchy.features.datatransport.DataTransportModule
import com.example.stretchy.features.traininglist.ui.TrainingListViewModel
import dagger.Component
import javax.inject.Provider

@TrainingListScope
@Component(
    dependencies = [ActivityComponent::class],
    modules = [TrainingListModule::class, DataTransportModule::class]
)
interface TrainingListComponent {
    @Component.Factory
    interface Factory {
        fun create(
            activityComponent: ActivityComponent,
        ): TrainingListComponent
    }

    fun viewModelProvider(): Provider<TrainingListViewModel>

    companion object {
        fun create(
            activityComponent: ActivityComponent,
        ): TrainingListComponent {
            return DaggerTrainingListComponent.factory().create(activityComponent)
        }
    }
}