package com.utsman.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar

fun Double?.orNol(): Double = this ?: 0.0
fun Int?.orNol(): Int = this ?: 0
fun Long?.orNol(): Long = this ?: 0L

fun ViewBinding.snackBar(message: String?) {
    Snackbar.make(root, message.orEmpty(), Snackbar.LENGTH_SHORT).show()
}

fun Context.isGrantedLocation(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

