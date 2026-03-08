package com.adobe.marketing.mobile.messagingsample

import android.app.Application
import com.adobe.marketing.mobile.Assurance
import com.adobe.marketing.mobile.Edge
import com.adobe.marketing.mobile.Messaging
import com.adobe.marketing.mobile.MobileCore
import com.adobe.marketing.mobile.LoggingMode
import com.adobe.marketing.mobile.Lifecycle
import com.adobe.marketing.mobile.edge.identity.Identity

class MessagingApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MobileCore.setApplication(this)
        MobileCore.setLogLevel(LoggingMode.VERBOSE)

        val extensions = listOf(
            Messaging.EXTENSION,
            Identity.EXTENSION,
            Lifecycle.EXTENSION,
            Edge.EXTENSION,
            Assurance.EXTENSION
        )

        MobileCore.registerExtensions(extensions) {
            if (BuildConfig.ADOBE_APP_ID.isNotBlank()) {
                MobileCore.configureWithAppID(BuildConfig.ADOBE_APP_ID)
                println("SUCCESS: Adobe Mobile SDK configured with App ID.")
            } else {
                println("ERROR: ADOBE_APP_ID is missing in .env.local. Adobe SDK will not work.")
            }

            if (BuildConfig.ADOBE_ASSURANCE_SESSION_ID.isNotBlank()) {
                Assurance.startSession(BuildConfig.ADOBE_ASSURANCE_SESSION_ID)
            }
        }
    }
}
