package com.example.vm.ui

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.vm.R
import com.example.vm.getOrAwaitValue
import com.example.vm.models.MathOperator
import com.example.vm.models.Status
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.reflect.Method

/**
 * Created by Atef on 13/02/21
 */

private const val TAG = "MainViewModelTest"

@RunWith(AndroidJUnit4::class)
class MainViewModelTest {

    @get: Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MainViewModel
    private lateinit var set: List<Method>

    @Before
    fun setupViewModel() {
        viewModel = MainViewModel(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun calculate_allValid_triggerEventOfValidEquation() {
        //GIVEN valid data
        viewModel.firstArg.value = "5"
        viewModel.secondArg.value = "5"
        viewModel.mathOperator.value = MathOperator.ADD
        viewModel.delay.value = "0"

        //WHEN calculate
        viewModel.calculate()

        val equation = viewModel.equation.getOrAwaitValue().getContentIfNotHandled()

        //THEN trigger valid equation event
        assertThat(equation?.first, `is`(5.0))
        assertThat(equation?.second, `is`(5.0))
        assertThat(equation?.operator, `is`(MathOperator.ADD))
        assertThat(equation?.delay, `is`(0))
    }

    @Test
    fun calculate_divideByZero_triggerDividedByZeroError() {
        //GIVEN divide by zero equation
        viewModel.firstArg.value = "5"
        viewModel.secondArg.value = "0"
        viewModel.mathOperator.value = MathOperator.DIV
        viewModel.delay.value = "0"

        //WHEN calculate
        viewModel.calculate()

        val errorMessage = viewModel.snackBarText.getOrAwaitValue().getContentIfNotHandled()
        //THEN trigger divided by zero snackBar
        assertThat(errorMessage, `is`(R.string.error_divide_by_zero))
    }

    @Test
    fun calculate_negativeDelay_triggerInvalidDelayMessage() {
        //GIVEN negative delay
        viewModel.firstArg.value = "5"
        viewModel.secondArg.value = "1"
        viewModel.mathOperator.value = MathOperator.ADD
        viewModel.delay.value = "-1"

        //WHEN calculate
        viewModel.calculate()

        val errorMessage = viewModel.snackBarText.getOrAwaitValue().getContentIfNotHandled()
        //THEN trigger invalid delay
        assertThat(errorMessage, `is`(R.string.error_invalid_delay))
    }

    @Test
    fun calculate_emptyDelay_triggerEventOfValidEquationOfZeroDelay() {
        //GIVEN valid equation of empty delay
        viewModel.firstArg.value = "5"
        viewModel.secondArg.value = "1"
        viewModel.mathOperator.value = MathOperator.ADD

        //WHEN calculate
        viewModel.calculate()

        val equation = viewModel.equation.getOrAwaitValue().getContentIfNotHandled()
        //THEN trigger valid equation of zero delay
        assertThat(equation?.delay, `is`(0))
    }

    @Test
    fun calculate_defaultOperatorSelection_add() {
        //GIVEN valid equation with no operator selection
        viewModel.firstArg.value = "5"
        viewModel.secondArg.value = "1"


        //WHEN calculate
        viewModel.calculate()

        val equation = viewModel.equation.getOrAwaitValue().getContentIfNotHandled()
        //THEN trigger valid equation of add operation
        assertThat(equation?.operator, `is`(MathOperator.ADD))
    }

    @Test
    fun calculate_emptyArg_triggerInvalidFirstArg() {
        //GIVEN empty arg

        //WHEN calculate
        viewModel.calculate()

        val errorMessage = viewModel.snackBarText.getOrAwaitValue().getContentIfNotHandled()
        //THEN trigger valid equation of add operation
        assertThat(errorMessage, `is`(R.string.error_invalid_first_argument))
    }

    @Test
    fun calculate_emptySecondArg_triggerInvalidFirstArg() {
        //GIVEN empty 2nd arg
        viewModel.firstArg.value = "5"

        //WHEN calculate
        viewModel.calculate()

        val errorMessage = viewModel.snackBarText.getOrAwaitValue().getContentIfNotHandled()
        //THEN trigger valid equation of add operation
        assertThat(errorMessage, `is`(R.string.error_invalid_second_arg))
    }

    @Test
    fun setNewResult_result_TriggerResult() {
        //GIVEN result string
        val result = "4.0 * 2.0 = 8.0"

        //WHEN set new result
        viewModel.setNewResult(result)

        val triggeredResult = viewModel.result.getOrAwaitValue()
        //THEN
        assertThat(triggeredResult, `is`(result))
    }

    @Test
    fun setStatus_status_TriggerStatus() {
        //GIVEN
        val status = Status.PENDING

        //WHEN
        viewModel.setStatus(status)

        val triggeredStatus = viewModel.status.getOrAwaitValue()
        //THEN
        assertThat(triggeredStatus, `is`(status))
    }

    @Test
    fun setNewLocation_validLocation_TriggerLocationText() {
        //GIVEN
        val location = Location("mock").apply {
            latitude = 22.3123123
            longitude = 21.123123
        }

        //WHEN
        viewModel.setNewLocation(location)

        val locationStr = viewModel.currentLocation.getOrAwaitValue()
        //THEN
        assertThat(locationStr, `is`("${location.latitude},${location.longitude}"))
    }

}