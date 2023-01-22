package com.utsman.ojeku

import android.content.Context
import android.content.Intent
import com.utsman.auth.ui.AuthActivity
import com.utsman.navigation.ActivityNavigatorCustomer
import com.utsman.navigation.intentTo
import com.utsman.ojeku.cust.search.SearchLocationActivity

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

    override fun searchLocationActivity(context: Context?, onIntent: (Intent) -> Unit) {
        context?.intentTo(SearchLocationActivity::class, onIntent)
    }

    companion object {
        fun build(): ActivityNavigatorCustomer {
            return ActivityNavigatorProvider()
        }
    }
}