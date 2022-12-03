package com.example.stretchy.extensions

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import javax.inject.Provider

inline fun <reified VM : ViewModel> ComponentActivity.daggerViewModel(
    owner: ViewModelStoreOwner,
    crossinline getProvider: () -> Provider<VM>,
) = lazy {
    ViewModelProvider(owner, getProvider().createFactory())[VM::class.java]
}

inline fun <reified VM : ViewModel> Provider<VM>.createFactory() =
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return this@createFactory.get() as T
        }
    }