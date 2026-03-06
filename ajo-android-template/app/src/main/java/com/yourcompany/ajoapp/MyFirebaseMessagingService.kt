package com.adobe.marketing.mobile.messagingsample

import android.util.Log
import com.adobe.marketing.mobile.MobileCore
import com.adobe.marketing.mobile.messaging.MessagingService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("MyFirebaseMessagingService", "Refreshed token: $token")
        MobileCore.setPushIdentifier(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("MyFirebaseMessagingService", "Message received from FCM.")

        if (MessagingService.handleRemoteMessage(this, message)) {
            Log.d("MyFirebaseMessagingService", "Message was handled by Adobe SDK.")
        } else {
            Log.d("MyFirebaseMessagingService", "Message not handled by Adobe SDK.")
        }
    }
}
