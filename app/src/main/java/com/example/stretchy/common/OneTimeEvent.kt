package com.example.stretchy.common

open class OneTimeEvent<T> {
    open var value: T? = null
    var isConsumed = false

    fun consume(): T? {
        return if (isConsumed) {
            return null
        } else {
            isConsumed = true
            value!!
        }
    }
}