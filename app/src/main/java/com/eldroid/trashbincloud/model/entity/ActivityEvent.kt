package com.eldroid.trashbincloud.model.entity

import android.os.Build
import androidx.annotation.RequiresApi
import com.eldroid.trashbincloud.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class ActivityEvent(
    // Firebase fields (primary constructor)
    var title: String = "",
    var message: String = "",
    var timestamp: String = "",
    var iconResource: Int = 0
) {
    // Secondary constructor for the old format
    @RequiresApi(Build.VERSION_CODES.O)
    constructor(
        type: EventType,
        iconResource: Int,
        time: LocalTime,
        detail: String,
        description: String,
        title: String,
        date: LocalDate
    ) : this(
        title = title,
        message = description,
        timestamp = date.atTime(time).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
        iconResource = iconResource
    )

    // Computed properties - nullable for safety
    val type: EventType?
        @RequiresApi(Build.VERSION_CODES.O)
        get() = when {
            title.contains("Auto Open", ignoreCase = true) -> EventType.AUTO_OPEN
            title.contains("Auto Close", ignoreCase = true) -> EventType.AUTO_CLOSE
            title.contains("Full", ignoreCase = true) ||
                    title.contains("nearly full", ignoreCase = true) -> EventType.BIN_FULL
            else -> EventType.AUTO_OPEN
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getLocalDateTime(): LocalDateTime? {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            LocalDateTime.parse(timestamp, formatter)
        } catch (e: Exception) {
            null
        }
    }

    val time: LocalTime?
        @RequiresApi(Build.VERSION_CODES.O)
        get() = getLocalDateTime()?.toLocalTime()

    val date: LocalDate?
        @RequiresApi(Build.VERSION_CODES.O)
        get() = getLocalDateTime()?.toLocalDate()

    val description: String?
        get() = message.takeIf { it.isNotEmpty() }

    val detail: String?
        get() = timestamp.takeIf { it.isNotEmpty() }

    // Icon based on type
    val icon: Int?
        @RequiresApi(Build.VERSION_CODES.O)
        get() {
            if (iconResource != 0) return iconResource

            return when (type) {
                EventType.AUTO_OPEN -> R.drawable.ic_open
                EventType.AUTO_CLOSE -> R.drawable.ic_auto_close
                EventType.BIN_FULL -> R.drawable.ic_warning
                null -> null
            }
        }
}

enum class EventType {
    AUTO_OPEN,
    AUTO_CLOSE,
    BIN_FULL
}