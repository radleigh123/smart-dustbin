package com.eldroid.trashbincloud.model.entity

import android.text.format.DateFormat
import android.graphics.Color
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class Notification(
    var notifId: String? = null,
    var userId: String? = null,
    var title: String? = null,
    var body: String? = null,
    var createdAt: Long? = null,
    var isRead: Boolean? = null,
    var binId: String? = null,
    var icon: String? = null,
    var color: String? = null,
    var type: String? = null
) {
    fun getFormattedTimestamp(): String {
        return if (createdAt == null) {
            "Never"
        } else {
            val date = Date(createdAt!!)
            val sdf = SimpleDateFormat("MMM dd, yyyy hh:mm:ss a", Locale.ENGLISH)
            sdf.timeZone = TimeZone.getTimeZone("Asia/Manila")
            sdf.format(date)
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
