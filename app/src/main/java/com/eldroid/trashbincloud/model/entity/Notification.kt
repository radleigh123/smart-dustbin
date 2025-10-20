package com.eldroid.trashbincloud.model.entity

import android.text.format.DateFormat
import com.google.firebase.Timestamp
import android.graphics.Color

data class Notification(
    var notifId: String? = null,
    var userId: String? = null,
    var title: String? = null,
    var body: String? = null,
    var createdAt: Timestamp? = null,
    var isRead: Boolean? = null,
    var binId: String? = null,
    var binName: Int? = null,
    var icon: String? = null,
    var color: String? = null,
    var type: String? = null
) {
    fun getFormattedTimestamp(): String {
        return if (createdAt == null) {
            "Never"
        } else {
            val date = createdAt!!.toDate()
            DateFormat.format("MMM dd, yyyy HH:mm:ss", date).toString()
        }
    }

    fun getStatusColor(): Int {
        return when (color) {
            "normal" -> Color.GREEN
            "warning" -> Color.YELLOW
            "critical" -> Color.RED
            else -> Color.GRAY
        }
    }
}
