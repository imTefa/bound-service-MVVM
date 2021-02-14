package com.example.vm.ui

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.vm.R
import com.example.vm.models.Equation
import com.example.vm.models.MathOperator
import com.example.vm.models.Status

/**
 *
 *Created by Atef on 12/02/21
 *
 */

private const val TAG = "MainViewModel"

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val x: Array<String> = arrayOf("")

    val firstArg = MutableLiveData("")
    private val firstDouble: LiveData<Double>
        get() = MutableLiveData(firstArg.value?.toDoubleOrNull())

    val secondArg = MutableLiveData("")
    private val secondDouble
        get() = MutableLiveData(secondArg.value?.toDoubleOrNull())

    val mathOperator = MutableLiveData(MathOperator.ADD)

    val delay = MutableLiveData("")
    private val delayInt
        get() = MutableLiveData(if (delay.value.isNullOrEmpty()) 0 else delay.value?.toInt())

    private val _snackBarText = MutableLiveData<Event<Int>>()
    val snackBarText: LiveData<Event<Int>> = _snackBarText

    private val _hideKeyboard = MutableLiveData<Event<Unit>>()
    val hideKeyboard: LiveData<Event<Unit>> = _hideKeyboard

    private val _equation = MutableLiveData<Event<Equation>>()
    val equation: LiveData<Event<Equation>> = _equation

    //TODO maybe it would be list better that textview
    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result

    private val _status = MutableLiveData(Status.NONE)
    val status: LiveData<Status> = _status

    private val _currentLocation = MutableLiveData<Location>()
    val currentLocation: LiveData<String> = Transformations.map(_currentLocation) { location ->
        if (location != null)
            "${location.latitude},${location.longitude}"
        else ""
    }


    fun calculate() {
        _hideKeyboard.value = Event(Unit)
        if (!validate())
            return

        val equation = Equation(
            first = firstDouble.value!!,
            second = secondDouble.value!!,
            operator = mathOperator.value!!,
            delay = delayInt.value!!
        )

        _equation.value = Event(equation)
    }

    private fun validate(): Boolean {

        return validateFirstArg() &&
                validateSecondArg() &&
                validateOperator() &&
                validateDelay() &&
                validateDivideOnZero()
    }

    private fun validateDivideOnZero(): Boolean {
        if (mathOperator.value == MathOperator.DIV && secondDouble.value == 0.0) {
            _snackBarText.value = Event(R.string.error_divide_by_zero)
            return false
        }
        return true

    }

    private fun validateDelay(): Boolean {
        val d = delayInt.value
        if (d == null || d < 0) {
            _snackBarText.value = Event(R.string.error_invalid_delay)
            return false
        }
        return true
    }

    private fun validateOperator(): Boolean {
        if (mathOperator.value == null) {
            _snackBarText.value = Event(R.string.error_invalid_operator)
            return false
        }
        return true
    }

    private fun validateSecondArg(): Boolean {
        if (secondDouble.value == null) {
            _snackBarText.value = Event(R.string.error_invalid_second_arg)
            return false
        }
        return true
    }

    private fun validateFirstArg(): Boolean {
        if (firstDouble.value == null) {
            _snackBarText.value = Event(R.string.error_invalid_first_argument)
            return false
        }
        return true
    }

    fun setNewResult(result: String) {
        _result.value = result
    }

    fun setStatus(status: Status) {
        _status.value = status
    }

    fun setNewLocation(location: Location) {
        _currentLocation.value = location
    }
}