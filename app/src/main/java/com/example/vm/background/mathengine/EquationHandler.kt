package com.example.vm.background.mathengine

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.example.vm.models.FIRST
import com.example.vm.models.KEY
import com.example.vm.models.SECOND

class EquationHandler(looper: Looper, private val callback: EquationCallback) : Handler(looper) {

    override fun handleMessage(msg: Message) {
        val arg1 = msg.data.getDouble(FIRST)
        val arg2 = msg.data.getDouble(SECOND)
        val key = msg.data.getLong(KEY)
        when (msg.what) {
            //Add
            0 -> add(arg1, arg2, key)
            1 -> subtract(arg1, arg2, key)
            2 -> multiply(arg1, arg2, key)
            3 -> divide(arg1, arg2, key)
            else -> throw Exception("Unknown operation exception")
        }
    }


    private fun add(arg1: Double, arg2: Double, key: Long) {
        val result = "$arg1 + $arg2 = ${arg1 + arg2}"
        callback.onEquationSolved(result,key)
    }

    private fun subtract(arg1: Double, arg2: Double, key: Long) {
        val result = "$arg1 - $arg2 = ${arg1 - arg2}"
        callback.onEquationSolved(result,key)
    }

    private fun multiply(arg1: Double, arg2: Double, key: Long) {
        val result = "$arg1 * $arg2 = ${arg1 * arg2}"
        callback.onEquationSolved(result,key)
    }

    private fun divide(arg1: Double, arg2: Double, key: Long) {
        if (arg2 == 0.0)
            throw ArithmeticException("Divide by zero exception")
        val result = "$arg1 / $arg2 = ${arg1 / arg2}"
        callback.onEquationSolved(result,key)
    }
}