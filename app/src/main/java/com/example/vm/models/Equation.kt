package com.example.vm.models

/**
 *
 *Created by Atef on 13/02/21
 *
 */

const val FIRST = "first"
const val SECOND = "second"
const val KEY = "key"

data class Equation(
    val first: Double,
    val second: Double,
    val operator: MathOperator,
    val delay: Int = 0
) {

    fun getLongDelay(): Long {
        return delay * 1000L
    }
}
