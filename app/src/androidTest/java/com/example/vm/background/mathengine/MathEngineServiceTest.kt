package com.example.vm.background.mathengine

import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ServiceTestRule
import com.example.vm.getOrAwaitValue
import com.example.vm.models.Equation
import com.example.vm.models.MathOperator
import com.example.vm.models.Status
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeoutException

/**
 * Created by Atef on 14/02/21
 */

@RunWith(AndroidJUnit4::class)
class MathEngineServiceTest {

    private lateinit var service: MathEngineService

    @get: Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val serviceRule = ServiceTestRule()

    @Before
    fun setupService() {
        val serviceIntent = Intent(
            ApplicationProvider.getApplicationContext<Context>(),
            MathEngineService::class.java
        )
        val binder: IBinder = serviceRule.bindService(serviceIntent)
        service = (binder as MathEngineService.LocalBinder).getService()
    }

    @Test
    @Throws(TimeoutException::class)
    fun testSetNewEquationToMessageQueue_validEquationWithZeroDelay_triggerResult() {
        //GIVEN valid equation with zero delay
        val equation = Equation(first = 5.0, second = 5.0, operator = MathOperator.ADD)

        //WHEN set new equation
        service.setNewEquationToMessageQueue(equation)
        val result = service.result.getOrAwaitValue().getContentIfNotHandled()

        //THEN trigger equation result
        val expected = "${equation.first} ${equation.operator.operator} ${equation.second} = 10.0"
        assertThat(result, `is`(expected))
    }

    @Test
    @Throws(TimeoutException::class)
    fun testStatus_validEquationWithDelay_statusChanged() {
        //GIVEN valid equation with zero delay
        val equation = Equation(first = 5.0, second = 5.0, operator = MathOperator.ADD, delay = 1)

        //status should be none
        var status = service.status.getOrAwaitValue()
        assertThat(status, `is`(Status.NONE))

        //WHEN set new equation
        service.setNewEquationToMessageQueue(equation)
        //THEN status should be pending
        status = service.status.getOrAwaitValue()
        assertThat(status, `is`(Status.PENDING))

        Thread.sleep(2000)

        //THEN should back to none
        status = service.status.getOrAwaitValue()
        assertThat(status, `is`(Status.NONE))
    }
}