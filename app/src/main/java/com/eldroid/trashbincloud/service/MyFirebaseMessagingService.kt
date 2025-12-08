package com.eldroid.trashbincloud.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.model.repository.NotifRepository
import com.eldroid.trashbincloud.view.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.UUID

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val notifRepository by lazy { NotifRepository() }

    override fun onNewToken(token: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            FirebaseDatabase.getInstance()
                .getReference("users")
                .child(user.uid)
                .child("fcmToken")
                .setValue(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: message.data["title"] ?: "TrashBin Alert"
        val body = message.notification?.body ?: message.data["body"] ?: "You have a new notification"
        val type = message.data["type"] ?: "info"
        val color = message.data["color"] ?: "normal"
        val icon = message.data["icon"] ?:   "default"
        val binId = message.data["binId"] ?: ""

        // Get user ID from payload or current logged-in user
        val userId = message.data["userId"] ?: FirebaseAuth.getInstance().currentUser?.uid
        if (userId.isNullOrEmpty()) {
            showLocalNotification(title, body, color)
            return
        }

        // ✅ Use push() to create a unique ID for every notification
//        val notifRef = FirebaseDatabase.getInstance()
//            .getReference("notifications")
//            .child(userId)
//            .push()
//
//        val notifId = notifRef.key ?: return

        // Save to Realtime Database
//        val notifId = UUID.randomUUID().toString()
//        val notifRef = FirebaseDatabase.getInstance()
//            .getReference("notifications")
//            .child(userId)
//            .child(notifId)
//
//        val notifData = mapOf(
//            "notifId" to notifId,
//            "userId" to userId,
//            "title" to title,
//            "body" to body,
//            "createdAt" to System.currentTimeMillis(),
//            "isRead" to false,
//            "binId" to binId,
//            "icon" to icon,
//            "color" to color,
//            "type" to type
//        )



        // ✅ Save notification data in Realtime Database
//        notifRef.setValue(notifData)
//            .addOnSuccessListener {
//                Log.d("FCM", "✅ Saved notification at path: notifications/$userId/$notifId")
//                // Verify it's actually there
//                notifRef.get().addOnSuccessListener {
//                    Log.d("FCM", "Verification read: ${it.value}")
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.e("FCM", "❌ Save failed: ${e.message}")
//            }

        // ✅ Create notification ID
        val notifId = UUID.randomUUID().toString()

        // ✅ Create Notification entity object
        val notification = com.eldroid.trashbincloud.model.entity.Notification(
            notifId = notifId,
            userId = userId,
            title = title,
            body = body,
            createdAt = System.currentTimeMillis(),
            isRead = false,
            binId = binId,
            icon = icon,
            color = color,
            type = type
        )

        // ✅ Save to Realtime Database using repository
        notifRepository.saveNotificationToRealtimeDatabase(userId, notification) { success, error ->
            if (success) {
                Log.d("FCM", "✅ Notification saved successfully via repository")
            } else {
                Log.e("FCM", "❌ Failed to save notification: $error")
            }
        }

        // ✅ Show local device notification
        showLocalNotification(title, body, color)
    }

//    private fun showLocalNotification(title: String, message: String) {
//        val channelId = "trashbin_channel"
//        val intent = Intent(this, Notification::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//
//        val pendingIntent = PendingIntent.getActivity(
//            this, 0, intent,
//            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        val notificationBuilder = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(R.drawable.notification_icon)
//            .setContentTitle(title)
//            .setContentText(message)
//            .setAutoCancel(true)
//            .setContentIntent(pendingIntent)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//
//        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // ✅ Create channel for Android 8.0+
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId,
//                "TrashBin Alerts",
//                NotificationManager.IMPORTANCE_HIGH
//            )
//            manager.createNotificationChannel(channel)
//        }
//
//        // ✅ Use a unique ID so multiple notifications appear separately
//        manager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
//    }

    private fun showLocalNotification(title: String, message: String, colorKey: String) {
        val channelId = "trashbin_channel"
        val intent = Intent(this, Notification::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // 🎨 Map cloud colors → actual Android colors
        val notifColor = when (colorKey) {
            "green" -> getColor(R.color.green)
            "yellow" -> getColor(R.color.yellow)
            "red" -> getColor(R.color.red)
            else -> getColor(R.color.gray)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(notifColor)         // 🔥 Apply color here
            .setColorized(true)           // 🔥 Enables color background for Android 8+

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "TrashBin Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        manager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

}
