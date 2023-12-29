package com.example.eshop.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.os.Build
import android.content.Context
import android.util.Log
import com.example.eshop.R
import com.example.eshop.models.NotificationData

class PushNotificationService : FirebaseMessagingService() {

    private val TAG = "PushNotificationService"
    private val buildNotificationID = "FCM_ID"
    private val notificationSequenceName = "FCM_NOTIFICATION"
    private lateinit var notificationManager: NotificationManager
    private lateinit var notification: Notification
    private lateinit var notificationBuilder: NotificationCompat.Builder


    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    // received message that sent from firebase cloud messaging with user id topic.
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        try {
            // convert data message to notification class.
            NotificationData(
                remoteMessage.data["userUID"]!!,
                remoteMessage.data["title"]!!,
                remoteMessage.data["message"]!!
            ).let {
                // create notification with title and message that sent from cloud.
                buildNotification(it.title, it.message)
            }
        } catch (e: Exception) {
            Log.d(TAG, "onMessageReceived: ${e.message}")
        }
    }

    private fun notificationChannelBuilder() {
        notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                buildNotificationID,
                notificationSequenceName,
                NotificationManager.IMPORTANCE_HIGH
            ).let {
                it.enableVibration(true)
                it.enableLights(true)
                notificationManager.createNotificationChannel(it)
            }
        }
    }

    fun buildNotification(title: String, message: String) {
        notificationChannelBuilder()
        notificationBuilder = NotificationCompat.Builder(this, buildNotificationID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(Notification.CATEGORY_SERVICE)
        notification = notificationBuilder.build()
        notificationManager.notify(1, notification)
    }
}