package com.eldroid.trashbincloud.model.entity
import android.text.format.DateFormat
import com.google.firebase.Timestamp

data class Notification(
    val notifId: String ?= null,
    val userId: String ?= null,
    val title: String ?= null,
    val body: String ?= null,
    val createdAt: Timestamp ?= null,
    val isRead: Boolean ?= null,
    val binId: String ?= null,
    val binName: Int ?= null,
    val icon: String ?= null,
    val color: String ?= null,
    val type: String ?= null
){
    fun getFormattedTimestamp(): String {
        return if (createdAt == null) {
            "Never"
        } else {
            val date = createdAt.toDate() // convert Timestamp → Date
            DateFormat.format("MMM dd, yyyy HH:mm:ss", date).toString()
        }
    }

    fun getStatusColor(): Int {
        return when (color) {
            "normal" -> android.graphics.Color.GREEN
            "warning" -> android.graphics.Color.YELLOW
            "critical" -> android.graphics.Color.RED
            else -> android.graphics.Color.GRAY
        }
    }
}
