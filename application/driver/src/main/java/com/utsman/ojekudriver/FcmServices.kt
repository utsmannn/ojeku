package com.utsman.ojekudriver

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.utsman.core.CoroutineBus
import com.utsman.core.data.longId
import com.utsman.core.extensions.IOScope
import com.utsman.core.extensions.onSuccess
import com.utsman.core.extensions.value
import com.utsman.network.ServiceMessage
import com.utsman.ojeku.booking.Booking
import com.utsman.ojeku.booking.BookingRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class FcmServices : FirebaseMessagingService() {
    private val bookingRepository: BookingRepository by inject()

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        println("ASUUUUU -> ${message.data}")

        val messageService = ServiceMessage.parseFromData(message.data)
        CoroutineBus.getInstance().post(messageService.type.name, messageService)

        IOScope().launch {
            when (messageService.type) {
                ServiceMessage.Type.BOOKING -> {
                    bookingRepository.getBookingById(messageService.bookingId)
                }
                else -> {}
            }

            bookingRepository.bookingCustomer
                .distinctUntilChangedBy { it.value?.status }
                .collect {
                    it.onSuccess {
                        handleBooking(this)
                    }
                }
        }
    }

    private fun handleBooking(booking: Booking) {
        when (booking.status) {
            Booking.BookingStatus.REQUEST -> {
                handleNotification(booking)
            }
            Booking.BookingStatus.CANCELED -> {
                NotificationManagerCompat.from(this).cancelAll()
            }
            else -> {}
        }
    }

    private fun handleNotification(booking: Booking) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("order", booking.id)
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, "ojeku-driver")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("Booking offer")
            .setContentText(booking.routeLocation.destination.name)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(
                booking.id.longId().toString().takeLast(4).toInt(),
                builder.build()
            )
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}