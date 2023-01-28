package com.utsman.ojekudriver

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.utsman.core.CoroutineBus
import com.utsman.network.ServiceMessage

class FcmServices : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        println("ASUUUUU -> ${message.data}")

        val messageService = ServiceMessage.parseFromData(message.data)
        CoroutineBus.getInstance().post(messageService.type.name, messageService)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}