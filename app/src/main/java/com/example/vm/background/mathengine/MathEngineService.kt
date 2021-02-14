package com.example.vm.background.mathengine

import android.app.Service
import android.content.Intent
import android.os.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.vm.utils.addItem
import com.example.vm.models.*
import com.example.vm.utils.removeItem
import com.example.vm.ui.Event
import java.util.*
import kotlin.collections.HashSet

private const val TAG = "MathEngineService"

class MathEngineService : Service(), EquationCallback {

    private val _result = MutableLiveData<Event<String>>()
    val result: LiveData<Event<String>> = _result

    private val binder = LocalBinder()
    private lateinit var handler: EquationHandler

    //Have a unique set of keys that says if there is a pending equations or not
    private val _set = MutableLiveData<HashSet<Long>>(HashSet())
    val status: LiveData<Status> = Transformations.map(_set) {
        if (it.isEmpty()) Status.NONE else Status.PENDING
    }

    override fun onCreate() {
        super.onCreate()
        handler = EquationHandler(Looper.myLooper()!!, this)
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    fun setNewEquationToMessageQueue(equation: Equation) {
        val key = System.currentTimeMillis() + equation.getLongDelay()
        val args = Bundle()
        args.putDouble(FIRST, equation.first)
        args.putDouble(SECOND, equation.second)
        args.putLong(KEY, key)
        val msg = Message()
        msg.what = equation.operator.ordinal
        msg.data = args
        handler.sendMessageDelayed(msg, equation.getLongDelay())
        _set.addItem(key)
    }


    inner class LocalBinder : Binder() {
        fun getService(): MathEngineService = this@MathEngineService
    }

    override fun onEquationSolved(result: String, key: Long) {
        _result.value = Event(result)
        _set.removeItem(key)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

}

fun interface EquationCallback {
    fun onEquationSolved(result: String, key: Long)
}

