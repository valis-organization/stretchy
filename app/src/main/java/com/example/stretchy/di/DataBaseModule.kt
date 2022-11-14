package com.example.stretchy.di

import com.example.stretchy.database.DataBase
import com.example.stretchy.database.MockedDataBaseImpl
import dagger.Module
import dagger.Provides

@Module
class DataBaseModule {
    @Provides
    fun provideDataBase() : DataBase{
        return MockedDataBaseImpl()
    }
}
