package com.eldroid.trashbincloud.presenter

import android.os.Build
import androidx.annotation.RequiresApi
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.contract.HistoryContract
import com.eldroid.trashbincloud.model.entity.ActivityEvent
import com.eldroid.trashbincloud.model.entity.EventType
import java.time.LocalDate
import java.time.LocalTime

class HistoryPresenter : HistoryContract.Presenter {

    private var view: HistoryContract.View? = null
    private var isFilterVisible = false
    private var currentFilterType: String = "all"
    private var currentBin: String = "All Bins"
    private var currentDate: LocalDate? = null
    private var allActivities: List<ActivityEvent> = emptyList()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun attachView(view: HistoryContract.View) {
        this.view = view
        allActivities = generateSampleData()
        loadWeeklyData()
        loadActivityList()
        loadBins()
    }

    override fun detachView() {
        view = null
    }

    override fun onFilterClicked() {
        isFilterVisible = !isFilterVisible
        view?.toggleFilterSection(isFilterVisible)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onEventTypeSelected(type: String) {
        currentFilterType = type
        view?.updateFilterButtons(type)
        applyFilters()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBinSelected(binName: String) {
        currentBin = binName
        applyFilters()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDateSelected(date: LocalDate?) {
        currentDate = date
        applyFilters()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun applyFilters() {
        var filteredActivities = allActivities

        if (currentFilterType != "all") {
            val desiredType = when (currentFilterType) {
                "open" -> EventType.AUTO_OPEN
                "close" -> EventType.AUTO_CLOSE
                "full" -> EventType.BIN_FULL
                else -> null
            }
            filteredActivities = filteredActivities.filter { it.type == desiredType }
        }
        if (currentBin != "All Bins") {
            filteredActivities = filteredActivities.filter {
                it.description.contains(currentBin, ignoreCase = true)
            }
        }
        currentDate?.let { selected ->
            filteredActivities = filteredActivities.filter { it.date == selected }
        }
        filteredActivities = filteredActivities.sortedWith(
            compareByDescending<ActivityEvent> { it.date }.thenByDescending { it.time }
        )

        filteredActivities = filteredActivities.take(10)


        view?.showActivityList(filteredActivities)
    }



    override fun loadWeeklyData() {
        val weeklyData = listOf(13, 8, 16, 10, 19, 5, 9)
        view?.showWeeklyData(weeklyData)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun loadActivityData() {
        view?.showActivityList(allActivities)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadActivityList() {
        view?.showActivityList(allActivities)
    }

    private fun loadBins() {
        val bins = listOf("All Bins", "Kitchen Bin", "Bathroom Bin", "Bedroom Bin", "Living Room Bin")
        view?.setupBinSpinner(bins)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateSampleData(): List<ActivityEvent> {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val twoDaysAgo = today.minusDays(2)
        val threeDaysAgo = today.minusDays(3)

        return listOf(
            // Today
            ActivityEvent(
                type = EventType.AUTO_OPEN,
                iconResource = R.drawable.ic_auto_open,
                time = LocalTime.of(14, 34),
                detail = "Motion detected",
                description = "Kitchen Bin opened automatically",
                title = "Auto Open",
                date = today
            ),
            ActivityEvent(
                type = EventType.AUTO_CLOSE,
                iconResource = R.drawable.ic_auto_close,
                time = LocalTime.of(12, 15),
                detail = "Button pressed",
                description = "Bathroom Bin closed automatically",
                title = "Auto Close",
                date = today
            ),
            ActivityEvent(
                type = EventType.BIN_FULL,
                iconResource = R.drawable.ic_bin_full,
                time = LocalTime.of(10, 20),
                detail = "Bin is full",
                description = "Kitchen Bin needs emptying",
                title = "Bin Full Alert",
                date = today
            ),
            ActivityEvent(
                type = EventType.AUTO_OPEN,
                iconResource = R.drawable.ic_auto_open,
                time = LocalTime.of(8, 30),
                detail = "Motion detected",
                description = "Bedroom Bin opened automatically",
                title = "Auto Open",
                date = today
            ),

            // Yesterday
            ActivityEvent(
                type = EventType.AUTO_CLOSE,
                iconResource = R.drawable.ic_auto_close,
                time = LocalTime.of(19, 45),
                detail = "Button pressed",
                description = "Kitchen Bin closed automatically",
                title = "Auto Close",
                date = yesterday
            ),
            ActivityEvent(
                type = EventType.BIN_FULL,
                iconResource = R.drawable.ic_bin_full,
                time = LocalTime.of(18, 0),
                detail = "Bin is full",
                description = "Living Room Bin needs emptying",
                title = "Bin Full Alert",
                date = yesterday
            ),
            ActivityEvent(
                type = EventType.AUTO_OPEN,
                iconResource = R.drawable.ic_auto_open,
                time = LocalTime.of(15, 10),
                detail = "Motion detected",
                description = "Bathroom Bin opened automatically",
                title = "Auto Open",
                date = yesterday
            ),
            ActivityEvent(
                type = EventType.AUTO_OPEN,
                iconResource = R.drawable.ic_auto_open,
                time = LocalTime.of(11, 25),
                detail = "Motion detected",
                description = "Kitchen Bin opened automatically",
                title = "Auto Open",
                date = yesterday
            ),
            ActivityEvent(
                type = EventType.AUTO_CLOSE,
                iconResource = R.drawable.ic_auto_close,
                time = LocalTime.of(9, 30),
                detail = "Button pressed",
                description = "Bedroom Bin closed automatically",
                title = "Auto Close",
                date = yesterday
            ),

            // 2 Days Ago
            ActivityEvent(
                type = EventType.BIN_FULL,
                iconResource = R.drawable.ic_bin_full,
                time = LocalTime.of(20, 15),
                detail = "Bin is full",
                description = "Bedroom Bin needs emptying",
                title = "Bin Full Alert",
                date = twoDaysAgo
            ),
        ).sortedWith(compareByDescending<ActivityEvent> { it.date }.thenByDescending { it.time })
    }
}