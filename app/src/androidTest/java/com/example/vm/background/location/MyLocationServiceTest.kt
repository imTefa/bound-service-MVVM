package com.example.vm.background.location

import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ServiceTestRule
import com.example.vm.getOrAwaitValue
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeoutException

/**
 * Created by Atef on 14/02/21
 */

//For this test don't forget to open gps

@RunWith(AndroidJUnit4::class)
class MyLocationServiceTest {

    private lateinit var service: MyLocationService

    @get: Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val serviceRule = ServiceTestRule()


    @Before
    fun setupService() {
        val serviceIntent = Intent(
            ApplicationProvider.getApplicationContext<Context>(),
            MyLocationService::class.java
        )
        val binder: IBinder = serviceRule.bindService(serviceIntent)
        service = (binder as MyLocationService.LocalBinder).getService()
    }

    @Test
    @Throws(TimeoutException::class)
    fun testBoundWithAppAndTriggerLocation() {
        //GIVEN nothing just run the service

        //WHEN nothing just wait for location
        val location = service.currentLocation.getOrAwaitValue(time = 5)

        //THEN trigger location
        assertThat(location, `is`(notNullValue()))
    }

}