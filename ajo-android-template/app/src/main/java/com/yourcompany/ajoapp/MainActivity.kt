package com.adobe.marketing.mobile.messagingsample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.adobe.marketing.mobile.MobileCore
import com.adobe.marketing.mobile.Messaging
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

/**
 * La actividad principal de la aplicación.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var triggerIamButton: Button
    private lateinit var iamTriggerEventEditText: EditText
    private lateinit var setPushIdButton: Button
    private lateinit var resetIdentitiesButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        triggerIamButton = findViewById(R.id.btnTriggerIam)
        iamTriggerEventEditText = findViewById(R.id.editTextImageTrigger)
        setPushIdButton = findViewById(R.id.btnSetPushIdentifier)
        resetIdentitiesButton = findViewById(R.id.btnResetIdentities)

        setupButtonClickListeners()
        askNotificationPermission()

        intent?.extras?.let {
            if (it.containsKey("messageId")) {
                Log.d("MainActivity", "App opened from a push notification.")
                Messaging.handleNotificationResponse(intent, true, null)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "Notification permission granted.")
        } else {
            Log.d("MainActivity", "Notification permission denied.")
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is already granted
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun setupButtonClickListeners() {
        setPushIdButton.setOnClickListener {
            Log.d("MainActivity", "Attempting to set push identifier...")
            lifecycleScope.launch {
                try {
                    val token = Firebase.messaging.token.await()
                    Log.d("MainActivity", "Obtained FCM Token: $token")
                    MobileCore.setPushIdentifier(token)
                    Toast.makeText(baseContext, "Push Identifier Set!", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Log.e("MainActivity", "ERROR: Failed to get FCM token.", e)
                    Toast.makeText(baseContext, "Error getting FCM token.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        triggerIamButton.setOnClickListener {
            val eventName = iamTriggerEventEditText.text.toString()
            if (eventName.isNotBlank()) {
                Log.d("MainActivity", "Tracking action: '$eventName'")
                MobileCore.trackAction(eventName, null)
                Toast.makeText(this, "Event '$eventName' sent.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter an event name.", Toast.LENGTH_SHORT).show()
            }
        }

        resetIdentitiesButton.setOnClickListener {
            Log.d("MainActivity", "Resetting identities...")
            MobileCore.resetIdentities()
            Toast.makeText(this, "Identities Reset.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        MobileCore.lifecycleStart(null)
    }

    override fun onPause() {
        super.onPause()
        MobileCore.lifecyclePause()
    }
}
