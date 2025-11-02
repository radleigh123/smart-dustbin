package com.eldroid.trashbincloud.model.entity

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class User(
    var name: String? = null,
    var email: String? = null,
    var address: String? = null,
    var role: String? = null,
    var contactNumber: String? = null,
    var fcmToken: String? = null,
    var createdAt: Long? = null,
    var lastUpdated: Long? = null
) {

    fun getLastUpdatedDate(): String {
        val date = Date(lastUpdated ?: 0)
        val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
        return formatter.format(date)
    }

    fun getCreatedAtDate(): String {
        val date = Date(createdAt ?: 0)
        val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
        return formatter.format(date)
    }

    fun setCreatedAtDate(dateString : String) {
        val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
        val parsedDate = formatter.parse(dateString)
        createdAt = parsedDate?.time
    }

    fun setLastUpdatedDate(dateString : String) {
        val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
        val parsedDate = formatter.parse(dateString)
        lastUpdated = parsedDate?.time
    }

}
