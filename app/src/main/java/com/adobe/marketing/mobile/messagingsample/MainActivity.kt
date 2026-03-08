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
import com.adobe.marketing.mobile.Event
import org.json.JSONObject
import java.util.HashMap

/**
 * La actividad principal de la aplicación.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var triggerIamButton: Button
    private lateinit var iamTriggerEventEditText: EditText
    private lateinit var setPushIdButton: Button
    private lateinit var resetIdentitiesButton: Button
    private lateinit var customDemoButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        triggerIamButton = findViewById(R.id.btnTriggerIam)
        iamTriggerEventEditText = findViewById(R.id.editTextImageTrigger)
        setPushIdButton = findViewById(R.id.btnSetPushIdentifier)
        resetIdentitiesButton = findViewById(R.id.btnResetIdentities)
        customDemoButton = findViewById(R.id.btnCustomDemo)

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

        customDemoButton.setOnClickListener {
            Log.d("MainActivity", "Triggering Custom AJO Demo Message...")
            showCustomAjoDemoMessage()
        }
    }

    private fun showCustomAjoDemoMessage() {
        val htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
                <style>
                    body { 
                        margin: 0; 
                        padding: 0; 
                        background-color: #FF6B00; 
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
                        display: flex; 
                        justify-content: center; 
                        align-items: center; 
                        height: 100vh; 
                        width: 100vw;
                    }
                    .card { 
                        background-color: white; 
                        border-radius: 20px; 
                        width: 90%; 
                        max-width: 350px; 
                        padding: 24px; 
                        box-shadow: 0 10px 30px rgba(0,0,0,0.3); 
                        text-align: center; 
                        position: relative;
                        animation: slideUp 0.5s ease-out;
                    }
                    @keyframes slideUp {
                        from { transform: translateY(50px); opacity: 0; }
                        to { transform: translateY(0); opacity: 1; }
                    }
                    .close-btn { 
                        position: absolute; 
                        top: 15px; 
                        right: 15px; 
                        font-size: 24px; 
                        color: #1473E6; 
                        text-decoration: none; 
                        font-weight: bold;
                        width: 30px;
                        height: 30px;
                        line-height: 30px;
                    }
                    .runner-img { 
                        width: 100%; 
                        border-radius: 12px; 
                        margin-bottom: 20px;
                        display: block;
                    }
                    h2 { color: #333; font-size: 22px; margin-bottom: 10px; margin-top: 10px; }
                    p { color: #666; font-size: 16px; margin-bottom: 25px; line-height: 1.5; }
                    .cta-btn { 
                        background-color: #1473E6; 
                        color: white; 
                        padding: 14px 0; 
                        width: 100%;
                        border-radius: 8px; 
                        text-decoration: none; 
                        display: block; 
                        font-weight: bold; 
                        font-size: 18px; 
                        box-shadow: 0 4px 10px rgba(20, 115, 230, 0.3);
                    }
                </style>
            </head>
            <body>
                <div class="card">
                    <a href="adbinapp://dismiss" class="close-btn">×</a>
                    <img src="https://images.unsplash.com/photo-1530143311094-34d807799e8f?q=80&w=600&auto=format&fit=crop" class="runner-img" alt="Runner">
                    <h2>Title text</h2>
                    <p>Sabes que a partir de ahora recibiras mensajes desde AJO</p>
                    <a href="adbinapp://dismiss" class="cta-btn">Button</a>
                </div>
            </body>
            </html>
        """.trimIndent()

        // Escaping HTML for JSON string
        val escapedHtml = htmlContent.replace("\"", "\\\"").replace("\n", "\\n")

        val payloadString = """
            {
              "items": [{
                "id": "demo-item-123",
                "schema": "https://ns.adobe.com/personalization/json-content-item",
                "data": {
                  "content": "{\"version\":1,\"rules\":[{\"condition\":{\"type\":\"group\",\"definition\":{\"conditions\":[{\"definition\":{\"key\":\"demo\",\"matcher\":\"eq\",\"values\":[\"true\"]},\"type\":\"matcher\"}],\"logic\":\"and\"}},\"consequences\":[{\"id\":\"demo-consequence-456\",\"type\":\"cjmiam\",\"detail\":{\"html\":\"$escapedHtml\",\"mobileParameters\":{\"schemaVersion\":\"1.0\",\"width\":100,\"height\":100,\"verticalAlign\":\"center\",\"horizontalAlign\":\"center\",\"uiTakeover\":true,\"displayAnimation\":\"bottom\",\"dismissAnimation\":\"bottom\",\"backdropColor\":\"#000000\",\"backdropOpacity\":0.5}}]}]}"
                }
              }]
            }
        """.trimIndent()

        Log.d("AJO_JSON_DEBUG", "Sending Mock AJO Payload: $payloadString")

        try {
            val payloadJson = JSONObject(payloadString)
            val eventData = HashMap<String, Any?>()
            eventData["payload"] = PayloadFormatUtils.toObjectMap(payloadJson)
            eventData["type"] = "personalization:decisions"

            val event = Event.Builder(
                "Custom Demo AJO Message",
                "com.adobe.eventType.edge",
                "personalization:decisions"
            ).setEventData(eventData).build()

            MobileCore.dispatchEvent(event)
            Toast.makeText(this, "Demo event dispatched! Check Logcat for JSON.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error creating demo message", e)
            Toast.makeText(this, "Error in demo: ${e.message}", Toast.LENGTH_LONG).show()
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
