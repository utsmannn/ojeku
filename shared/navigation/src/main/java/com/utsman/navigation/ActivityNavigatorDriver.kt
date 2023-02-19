package com.utsman.navigation

import android.content.Context

interface ActivityNavigatorDriver {
    fun mainActivity(context: Context?)
    fun authActivityDriver(context: Context?)
}