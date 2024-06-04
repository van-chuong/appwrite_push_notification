package com.example.appwritepushnoti

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.services.Account
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {
    private var token: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
//        createDefaultChannel()
        // Bắt đầu một coroutine để lấy token từ Firebase và gọi createPushTarget
        CoroutineScope(Dispatchers.Main).launch {
            getTokenAndCreatePushTarget()
        }
    }

    private suspend fun getTokenAndCreatePushTarget() {
        // Lấy token từ Firebase
        try {
            token = FirebaseMessaging.getInstance().token.await()
            Log.d("Token", token)
            // Gọi createPushTarget
//            createPushTarget()
        } catch (e: Exception) {
            Log.e("Token", "Error getting Firebase token", e)
        }
    }

    private suspend fun createPushTarget() {
        val client = Client(applicationContext)
            .setEndpoint("https://cloud.appwrite.io/v1")
            .setProject("663b932300030cf0a819")
            .setSelfSigned(true)
        val account = Account(client)
        account.createEmailPasswordSession(
            "chuong@gmail.com", "12345678"
        )
        val result = account.createPushTarget(ID.unique(), token, "663b994c00244d2f2325")
        Log.d("Token", result.toString())
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createDefaultChannel(){
        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()

        val notificationChannel = NotificationChannel("2002", "default", NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/raw/sound" ), audioAttributes)
        notificationChannel.setVibrationPattern(longArrayOf(250, 250, 250, 250))
        notificationChannel.enableLights(true)
        notificationChannel.enableVibration(true)
        notificationChannel.lightColor = Color.RED
        mNotificationManager.createNotificationChannel(notificationChannel)
    }
}
