package com.utsman.ojeku

import android.content.Context
import com.utsman.auth.ui.AuthActivity
import com.utsman.navigation.ActivityNavigatorCustomer
import com.utsman.navigation.intentTo

class ActivityNavigatorProvider : ActivityNavigatorCustomer {
    override fun mainActivity(context: Context?) {
        context?.intentTo(MainActivity::class)
    }

    override fun authActivityCustomer(context: Context?) {
        context?.intentTo(AuthActivity::class) {
            it.putExtra("type", "customer")
        }
    }

    override fun authActivityDriver(context: Context?) {
        context?.intentTo(AuthActivity::class) {
            it.putExtra("type", "driver")
        }
    }

    companion object {
        fun build(): ActivityNavigatorCustomer {
            return ActivityNavigatorProvider()
        }
    }
}