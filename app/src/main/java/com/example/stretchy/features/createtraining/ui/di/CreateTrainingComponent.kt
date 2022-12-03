package com.example.stretchy.features.createtraining.ui.di

import com.example.stretchy.activity.di.ActivityComponent
import com.example.stretchy.features.createtraining.ui.CreateTrainingViewModel
import dagger.Component
import javax.inject.Provider

@CreateTrainingScope
@Component(
    dependencies = [ActivityComponent::class],
    modules = [CreateTrainingModule::class]
)
interface CreateTrainingComponent {
    @Component.Factory
    interface Factory {
        fun create(
            activityComponent: ActivityComponent,
        ): CreateTrainingComponent
    }

    fun viewModelProvider(): Provider<CreateTrainingViewModel>

    companion object {
        fun create(
            activityComponent: ActivityComponent,
        ): CreateTrainingComponent {
            return DaggerCreateTrainingComponent.factory().create(activityComponent)
        }
    }
}