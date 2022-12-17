package com.example.stretchy.features.executetraining.di

import androidx.activity.ComponentActivity
import com.example.stretchy.features.executetraining.sound.Player
import com.example.stretchy.features.executetraining.ui.ExecuteTrainingViewModel
import com.example.stretchy.repository.Repository
import dagger.Module
import dagger.Provides

@Module
class ExecuteTrainingModule {
    @Provides
    fun provideExecuteTrainingViewModel(repository: Repository, trainingId: Long) =
        ExecuteTrainingViewModel(repository, trainingId)

    @ExecuteTrainingScope
    @Provides
    fun providePlayer(componentActivity: ComponentActivity): Player =
        Player(componentActivity.baseContext)
}