package com.example.stretchy

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class Timer {
    private var _counterFlow: Flow<Float>? = null
    var flow: MutableStateFlow<Float> = MutableStateFlow(0f)
    private var currentMs: Int = 0
    private var paused = true

    init {
        _counterFlow = (0..Int.MAX_VALUE)
            .asSequence()
            .asFlow()
            .map { it.toFloat() }
            .onEach {
                delay(10)
                if (!paused) {
                    currentMs -= 10
                    flow.emit(currentMs.toFloat())
                }
            }
        GlobalScope.launch {
            _counterFlow!!.collect {}
        }
    }

    fun start() {
        paused = false
    }

    fun pause() {
        paused = true
    }

    fun setSeconds(seconds: Int) {
        flow.value = seconds.toFloat() * 1000
        currentMs = seconds * 1000
    }
}