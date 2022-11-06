package com.example.stretchy.repository

sealed class ActivityDomain(open var duration: Int) {
    class ExerciseDomain(
        val name: String,
        override var duration: Int
    ) : ActivityDomain(duration)

    class BreakDomain(override var duration: Int) : ActivityDomain(duration)
}