package com.utsman.ojeku

import com.google.firebase.messaging.FirebaseMessaging

object MainUtils {

    fun getFcmToken(onResult: (String) -> Unit) {
        FirebaseMessaging.getInstance()
            .token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult.invoke(task.result)
                } else {
                    onResult.invoke("")
                }
            }
    }
}