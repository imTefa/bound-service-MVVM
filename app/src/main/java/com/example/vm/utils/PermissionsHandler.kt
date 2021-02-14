package com.example.vm.utils

import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.vm.R
import com.example.vm.background.location.createLocationRequest
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task

/**
 *
 *Created by Atef on 14/02/21
 *
 */

const val DEFAULT_PERMISSION_REQUEST_CODE = 111;
const val REQUEST_CHECK_SETTINGS = 112;

fun checkIfGranted(context: Context, vararg permissions: String): Boolean {
    for (permission in permissions)
        if (!checkIfGranted(context, permission))
            return false
    return true
}

fun checkIfGranted(grantResults: IntArray): Boolean {
    for (result in grantResults)
        if (result != PERMISSION_GRANTED)
            return false
    return true
}

private fun checkIfGranted(context: Context, permission: String): Boolean {
    return PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
        context,
        permission
    )
}


fun requestPermission(
    activity: Activity,
    permissions: Array<String>,
    requestCode: Int = DEFAULT_PERMISSION_REQUEST_CODE
) {
    ActivityCompat.requestPermissions(activity, permissions, requestCode)
}

fun shouldShowRational(activity: Activity, vararg permissions: String): Boolean {
    for (permission in permissions)
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission))
            return true
    return false
}

fun showRationalDialog(
    context: Context,
    @StringRes message: Int,
    allow: () -> Unit,
    deny: () -> Unit
) {
    AlertDialog.Builder(context)
        .setMessage(message)
        .setPositiveButton(R.string.btn_allow) { _, _ -> allow() }
        .setNegativeButton(R.string.btn_deny) { _, _ -> deny() }
        .show()
}


fun checkLocationSettings(context: Activity, done: () -> Unit) {

    val builder = LocationSettingsRequest.Builder()
        .addLocationRequest(createLocationRequest())
    val client: SettingsClient = LocationServices.getSettingsClient(context)
    val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

    task.addOnSuccessListener { done() }

    task.addOnFailureListener { exception ->
        if (exception is ResolvableApiException) {
            // Location settings are not satisfied, but this can be fixed
            // by showing the user a dialog.
            try {
                // Show the dialog by calling startResolutionForResult(),
                // and check the result in onActivityResult().
                exception.startResolutionForResult(
                    context,
                    REQUEST_CHECK_SETTINGS
                )
            } catch (sendEx: IntentSender.SendIntentException) {
                // Ignore the error.
            }
        }
    }
}

