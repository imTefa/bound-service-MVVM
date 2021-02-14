package com.example.vm.utils

import androidx.lifecycle.MutableLiveData

/**
 *
 *Created by Atef on 13/02/21
 *
 */

fun <T> MutableLiveData<HashSet<T>>.addItem(value: T) {
    this.value?.add(value)
    manualNotify()
}


fun <T> MutableLiveData<HashSet<T>>.removeItem(value: T) {
    this.value?.remove(value)
    manualNotify()
}


fun <T> MutableLiveData<HashSet<T>>.manualNotify() {
    this.value = this.value
}
