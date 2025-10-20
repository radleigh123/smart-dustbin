package com.eldroid.trashbincloud.model.repository

import android.util.Log
import com.eldroid.trashbincloud.model.entity.Notification
import com.google.firebase.firestore.FirebaseFirestore

class NotifRepository (private val db: FirebaseFirestore = FirebaseFirestore.getInstance()){

    fun getNotifications(userId: String, callback: (List<Notification>, String?) -> Unit) {
        db.collection("notifications")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val notifications = documents.map { it.toObject(Notification::class.java) }
                    callback(notifications, null)
                } else {
                    callback(emptyList(), null)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("NotifRepository", "Error getting notifications", exception)
                callback(emptyList(), exception.message)
            }
    }


    fun getUnreadNotif(userId: String, callback: (Int, String?) -> Unit) {
        db.collection("notifications")
            .whereEqualTo("userId", userId)
            .whereEqualTo("isRead", false)
            .get()
            .addOnSuccessListener { documents ->
                val unreadCount = documents.size()
                callback(unreadCount, null)
            }
            .addOnFailureListener { exception ->
                callback(0, exception.message)
            }
    }
}