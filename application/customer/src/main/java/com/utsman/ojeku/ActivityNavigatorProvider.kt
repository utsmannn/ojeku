package com.utsman.ojeku

import android.content.Context
import com.utsman.auth.ui.AuthActivity
import com.utsman.navigation.ActivityNavigatorCustomer
import com.utsman.navigation.intentTo

class ActivityNavigatorProvider : ActivityNavigatorCustomer {
    override fun mainActivity(context: Context?) {
        context?.intentTo(MainActivity::class)
    }

    override fun authActivity(context: Context?) {
        context?.intentTo(AuthActivity::class)
    }

    companion object {
        fun build(): ActivityNavigatorCustomer {
            return ActivityNavigatorProvider()
        }
    }
}