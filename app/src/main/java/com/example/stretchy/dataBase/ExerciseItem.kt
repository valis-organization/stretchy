package com.example.stretchy.dataBase


sealed class ActivityRepo(open var duration: Int) {
    class ExerciseRepo(
        val name: String,
        override var duration: Int
    ) : ActivityRepo(duration)

    class BreakRepo(override var duration: Int) : ActivityRepo(duration)

}