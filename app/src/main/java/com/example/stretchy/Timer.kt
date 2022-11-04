package com.example.stretchy

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class Timer{
    private var _counterFlow: Flow<Int>? = null
    var flow: MutableStateFlow<Int> = MutableStateFlow(0)
    private var currentMs: Int = 0
    private var paused = true

    init {
        _counterFlow = (0..Int.MAX_VALUE)
            .asSequence()
            .asFlow()
            .onEach {
                delay(10)
                if (!paused) {
                    currentMs -= 10
                    if (it.mod(100) == 0) {
                        flow.emit(currentMs / 1000)
                    }
                }
            }
        GlobalScope.launch{
                _counterFlow!!.collect {}
        }
    }

    fun start() {
        paused = false
    }

    fun pause() {
        paused = true
    }

    fun setSeconds(seconds : Int){
        flow.value = seconds
        currentMs = seconds * 1000
    }

    companion object {
        private const val ONE_SECOND = 1_000L
    }
}