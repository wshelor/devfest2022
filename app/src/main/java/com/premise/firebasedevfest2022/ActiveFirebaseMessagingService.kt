package com.premise.firebasedevfest2022

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.flow.MutableStateFlow

class ActiveFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        dataState.value = message
    }
    companion object {
        val dataState = MutableStateFlow<RemoteMessage?>(null)
    }
}