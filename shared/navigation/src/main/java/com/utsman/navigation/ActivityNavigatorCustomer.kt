package com.utsman.navigation

import android.content.Context

interface ActivityNavigatorCustomer {

    fun mainActivity(context: Context?)
    fun authActivity(context: Context?)
}