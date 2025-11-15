package com.eldroid.trashbincloud.model.repository

import android.util.Log
import com.eldroid.trashbincloud.model.entity.Notification
import com.google.firebase.database.*
import kotlin.time.Duration.Companion.seconds

class NotifRepository(
    private val db: DatabaseReference = FirebaseDatabase.getInstance().getReference("notifications")
) {

    fun getNotifications(userId: String, callback: (List<Notification>, String?) -> Unit) {
        db.child(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val notifications = mutableListOf<Notification>()
                    for (notifSnap in snapshot.children) {
                        val notif = notifSnap.getValue(Notification::class.java)
                        notif?.let { notifications.add(it) }
                    }
//                    callback(notifications.sortedByDescending { it.createdAt?.seconds }, null)
                    callback(notifications.sortedByDescending { it.createdAt }, null)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList(), error.message)
                }
            })
    }


    fun getUnreadNotif(userId: String, callback: (Int, String?) -> Unit) {
        db.child(userId)
            .orderByChild("isRead")
            .equalTo(false)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback(snapshot.childrenCount.toInt(), null)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(0, error.message)
                }
            })
    }

    fun markAsRead(userId: String, notifId: String) {
        db.child(userId).child(notifId).child("isRead").setValue(true)
            .addOnSuccessListener {
                Log.d("NotifRepository", "Notification $notifId marked as read.")
            }
            .addOnFailureListener {
                Log.e("NotifRepository", "Failed to mark as read: ${it.message}")
            }
    }

    fun saveNotificationToRealtimeDatabase(
        userId: String,
        notification: Notification,
        callback: (Boolean, String?) -> Unit
    ) {
        val notifId = notification.notifId ?: return callback(false, "Notification ID is null")

        db.child(userId)
            .child(notifId)
            .setValue(notification)
            .addOnSuccessListener {
                Log.d("NotifRepository", "✅ Notification saved: $notifId")
                callback(true, null)
            }
            .addOnFailureListener { e ->
                Log.e("NotifRepository", "❌ Failed to save notification: ${e.message}")
                callback(false, e.message)
            }
    }

    fun markAllAsRead(userId: String, callback: (Boolean, String?) -> Unit) {
        val notificationsRef = FirebaseDatabase.getInstance()
            .getReference("notifications")
            .child(userId)

        notificationsRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val updates = mutableMapOf<String, Any>()
                snapshot.children.forEach { notifSnapshot ->
                    val notifId = notifSnapshot.key
                    if (notifId != null) {
                        updates["$notifId/isRead"] = true
                    }
                }

                if (updates.isNotEmpty()) {
                    notificationsRef.updateChildren(updates)
                        .addOnSuccessListener { callback(true, null) }
                        .addOnFailureListener { callback(false, it.message) }
                } else {
                    callback(true, null)
                }
            } else {
                callback(true, null)
            }
        }.addOnFailureListener {
            callback(false, it.message)
        }
    }

    fun markAllAsUnread(userId: String, callback: (Boolean, String?) -> Unit) {
        val notificationsRef = FirebaseDatabase.getInstance()
            .getReference("notifications")
            .child(userId)

        notificationsRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val updates = mutableMapOf<String, Any>()
                snapshot.children.forEach { notifSnapshot ->
                    val notifId = notifSnapshot.key
                    if (notifId != null) {
                        updates["$notifId/isRead"] = false
                    }
                }

                if (updates.isNotEmpty()) {
                    notificationsRef.updateChildren(updates)
                        .addOnSuccessListener { callback(true, null) }
                        .addOnFailureListener { callback(false, it.message) }
                } else {
                    callback(true, null)
                }
            } else {
                callback(true, null)
            }
        }.addOnFailureListener {
            callback(false, it.message)
        }
    }
}
