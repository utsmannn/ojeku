package com.utsman.ojekudriver

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.ojeku.profile.di.ProfileModule
import com.utsman.driver.home.HomeModule
import com.utsman.koin.KoinStarter
import com.utsman.ojeku.booking.BookingModule
import com.utsman.ojeku.socket.SocketModule

class MainDriver : Application() {

    override fun onCreate() {
        super.onCreate()
        KoinStarter.onCreate(this, listOf(
            MainModule.modules(),
            HomeModule.modules(),
            BookingModule.modules(),
            ProfileModule.modules(),
            SocketModule.modules()
        ))

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("ojeku-driver", "notification booking", importance).apply {
                description = "notification when booking from customer"
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}