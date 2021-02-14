package com.example.vm.models

import com.example.vm.models.MathOperator.*


/**
 *
 *Created by Atef on 13/02/21
 *
 */

val operatorsArray = arrayOf(ADD.operator, SUB.operator, MUL.operator, DIV.operator)

fun getMathOperator(position: Int): MathOperator? {
    return when (position) {
        ADD.ordinal -> ADD
        SUB.ordinal -> SUB
        MUL.ordinal -> MUL
        DIV.ordinal -> DIV

        //NEVER HAPPENED
        else -> null
    }
}

enum class MathOperator(val operator: String) {
    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/");
}