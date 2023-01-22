package com.utsman.ojekudriver

import android.content.Context
import com.utsman.auth.ui.AuthActivity
import com.utsman.navigation.ActivityNavigatorDriver
import com.utsman.navigation.intentTo

class ActivityNavigatorProvider : ActivityNavigatorDriver {
    override fun mainActivity(context: Context?) {
        context?.intentTo(MainActivity::class)
    }

    override fun authActivityDriver(context: Context?) {
        context?.intentTo(AuthActivity::class) {
            it.putExtra("type", "driver")
        }
    }

    companion object {
        fun build(): ActivityNavigatorDriver {
            return ActivityNavigatorProvider()
        }
    }
}