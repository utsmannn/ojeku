package com.utsman.ojeku

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ojeku.profile.entity.User
import com.utsman.core.CoroutineBus
import com.utsman.core.extensions.IOScope
import com.utsman.network.ServiceMessage
import com.utsman.ojeku.socket.SocketWrapper
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FcmServices : FirebaseMessagingService() {

    override fun onCreate() {
        super.onCreate()

        /*IOScope().launch {
            CoroutineBus.getInstance().getFlow<User>("user_to_event", IOScope())
                .collect { user ->
                    SocketWrapper.instance.listen(user.username) {
                        println("asuuuuuuuu data incoming from event")
                    }
                }
        }*/
    }

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