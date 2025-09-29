package com.eldroid.trashbincloud.model.entity

/**
 * Data class representing a trash bin with its current status and measurements.
 *
 * @property binId Unique identifier for the trash bin
 * @property name Display name of the trash bin
 * @property location Physical location description of the trash bin
 * @property fillLevel Current fill level as a percentage (0-100)
 * @property status Current status of the bin ("normal", "warning", "critical", "offline")
 * @property lastUpdated Timestamp of the last data update (in milliseconds since epoch)
 * @property battery Current battery level of the sensor (percentage 0-100)
 * @property temperature Current temperature reading inside the bin (in celsius)
 */
data class TrashBin(
    val binId: String = "",
    val name: String = "",
    val location: String = "",
    val fillLevel: Int = 0,
    val status: String = "offline",
    val lastUpdated: Long = 0,
    val battery: Int = 0,
    val temperature: Float = 0.0f,
    val lastEmptied: String,
    val daysToFill: Double

) {
    // Helper function to get formatted timestamp
    fun getFormattedTimestamp(): String {
        if (lastUpdated == 0L) return "Never"
        val date = java.util.Date(lastUpdated)
        return android.text.format.DateFormat.format("MMM dd, yyyy HH:mm:ss", date).toString()
    }

    // Helper function to get status color
    fun getStatusColor(): Int {
        return when (status) {
            "normal" -> android.graphics.Color.GREEN
            "warning" -> android.graphics.Color.YELLOW
            "critical" -> android.graphics.Color.RED
            else -> android.graphics.Color.GRAY
        }
    }
}
