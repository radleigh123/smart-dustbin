package com.eldroid.trashbincloud.model.entity

data class TrashBin(
    var binId: String? = null,
    var name: String? = null,
    var location: String? = null,
    var distance: Int? = null,
    var fillLevel: Int? = null,
    var status: String? = null,
    var lastUpdated: Long? = null,
    var battery: Int? = null,
    var temperature: Float? = null,
    var lastEmptied: String? = null,
    var daysToFill: Double? = null

) {
    // Helper function to get formatted timestamp
    fun getFormattedTimestamp(): String {
        if (lastUpdated == 0L) return "Never"
        val date = java.util.Date(lastUpdated ?: 0)
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
