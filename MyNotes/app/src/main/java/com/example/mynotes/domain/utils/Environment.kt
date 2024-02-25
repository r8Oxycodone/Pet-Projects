package com.example.mynotes.domain.utils

import kotlin.random.Random

class Environment {
    companion object {
        var colorState = 0
        var colorCode = Random.nextInt()
    }
}