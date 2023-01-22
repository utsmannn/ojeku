package com.utsman.ojekudriver

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FcmServices : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        println("ASUUUUU -> ${message.data}")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}