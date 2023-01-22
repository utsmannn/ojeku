package com.utsman.navigation

import android.content.Context
import android.content.Intent

interface ActivityNavigatorCustomer {

    fun mainActivity(context: Context?)
    fun authActivityCustomer(context: Context?)
    fun authActivityDriver(context: Context?)
    fun searchLocationActivity(context: Context?, onIntent: (Intent) -> Unit)
}