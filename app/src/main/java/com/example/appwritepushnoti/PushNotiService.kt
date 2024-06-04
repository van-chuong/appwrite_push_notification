package com.example.appwritepushnoti

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class PushNotiService : FirebaseMessagingService() {
    // Should not change the channel ID's
    private val DEFAULT_VIBRATE_PATTERN = longArrayOf(250, 250, 250, 250)
    private var SOUND_URI: Uri? = null
    private var channelId: String? = null

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("NOTIFICATION", message.getNotification()?.title.toString())
        sendMyNotification(message)
    }

    private fun sendMyNotification(message: RemoteMessage) {
        channelId = message.data["channel_id"]
        SOUND_URI = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/raw/${message.data["sound"]}" )
        Log.d("NOTIFICATION", message.data.toString())

        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId!!)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(if (message.getNotification()?.title == null) "app" else message.getNotification()!!.title)
            .setContentText(if (message.getNotification()?.body == null) "" else message.getNotification()!!.body)
            .setAutoCancel(true)
            .setSound(SOUND_URI)

        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.deleteNotificationChannel(channelId)
            // Creating an Audio Attribute
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            // Creating Channel
            val notificationChannel = NotificationChannel(channelId, "Testing_Audio", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.setSound(SOUND_URI, audioAttributes)
            notificationChannel.setVibrationPattern(DEFAULT_VIBRATE_PATTERN)
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.lightColor = Color.RED
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
        mNotificationManager.notify(0, notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("Token", token)
    }
}