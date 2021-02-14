package com.example.vm.ui

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.vm.R
import com.example.vm.background.location.MyLocationService
import com.example.vm.background.mathengine.MathEngineService
import com.example.vm.databinding.ActivityMainBinding
import com.example.vm.hideKeyboard
import com.example.vm.setupSnackBar
import com.example.vm.showSnackbar
import com.example.vm.utils.*
import com.google.android.material.snackbar.Snackbar


private const val TAG = "MainActivity"

val locationPermission =
    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()


    private lateinit var mathEngineService: MathEngineService
    private var engineBound: Boolean = false

    private val engineServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as MathEngineService.LocalBinder
            mathEngineService = binder.getService()
            engineBound = true
            setupServiceObservers()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            engineBound = false
        }
    }

    private lateinit var locationServices: MyLocationService
    private var locationBound: Boolean = false

    private val locationServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as MyLocationService.LocalBinder
            locationServices = binder.getService()
            locationBound = true
            setupLocationObservers()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            locationBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setupSnackBar()
        setupObservers()
        setupLocationService()
    }

    override fun onStart() {
        super.onStart()
        Intent(this, MathEngineService::class.java).also { intent ->
            bindService(intent, engineServiceConnection, Context.BIND_AUTO_CREATE)
        }

    }

    private fun startLocationService() {
        Intent(this, MyLocationService::class.java).also { intent ->
            bindService(intent, locationServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun setupLocationObservers() {
        locationServices.currentLocation.observe(this) {
            it?.let { viewModel.setNewLocation(it) }
        }
    }


    override fun onStop() {
        super.onStop()

        unbindService(engineServiceConnection)
        engineBound = false

        unbindService(locationServiceConnection)
        locationBound = false
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, MathEngineService::class.java))
        stopService(Intent(this, MyLocationService::class.java))
    }

    private fun setupObservers() {
        viewModel.equation.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                if (engineBound)
                    mathEngineService.setNewEquationToMessageQueue(it)
            }
        }

        viewModel.hideKeyboard.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                hideKeyboard()
            }
        }
    }

    private fun setupServiceObservers() {
        mathEngineService.result.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                viewModel.setNewResult(it)
            }
        }

        mathEngineService.status.observe(this) {
            if (it != null)
                viewModel.setStatus(it)
        }
    }

    private fun setupSnackBar() {
        binding.parentView.setupSnackBar(this, viewModel.snackBarText, Snackbar.LENGTH_SHORT)
    }


    private fun setupLocationService() {
        when {
            checkIfGranted(this, *locationPermission) -> {
                checkLocationSettings()
            }
            shouldShowRational(this, *locationPermission) -> {
                showRationalDialog(
                    this,
                    R.string.location_permission_rational,
                    { requestPermissions() },
                    { showLocationWontUpdateSnackBar() }
                )
            }
            else -> requestPermissions()
        }
    }

    private fun requestPermissions() {
        requestPermission(this, locationPermission)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            DEFAULT_PERMISSION_REQUEST_CODE -> {
                if (checkIfGranted(grantResults))
                    checkLocationSettings()
                else showLocationWontUpdateSnackBar()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> {
                if (resultCode == RESULT_OK)
                    startLocationService()
                else showLocationWontUpdateSnackBar()
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun checkLocationSettings() {
        checkLocationSettings(this) {
            startLocationService()
        }
    }


    private fun showLocationWontUpdateSnackBar() {
        binding.parentView.showSnackbar(
            getString(R.string.current_location_wont_updated),
            Snackbar.LENGTH_LONG
        )
    }
}