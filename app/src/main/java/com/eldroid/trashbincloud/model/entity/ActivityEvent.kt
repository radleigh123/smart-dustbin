package com.eldroid.trashbincloud.model.entity

import java.time.LocalDate
import java.time.LocalTime

data class ActivityEvent(
    val type: EventType,
    val iconResource: Int,
    val time: LocalTime,
    val detail: String,
    val description: String,
    val title: String,
    val date: LocalDate
)

enum class EventType {
    AUTO_OPEN,
    MANUAL_OPEN,
    BIN_FULL,
    HOLD_MODE
}
