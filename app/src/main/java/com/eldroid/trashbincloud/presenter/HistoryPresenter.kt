package com.eldroid.trashbincloud.presenter

import com.eldroid.trashbincloud.R
import android.os.Build
import androidx.annotation.RequiresApi
import com.eldroid.trashbincloud.contract.HistoryContract
import com.eldroid.trashbincloud.model.entity.ActivityEvent
import com.eldroid.trashbincloud.model.entity.EventType
import java.time.LocalDate
import java.time.LocalTime

class HistoryPresenter : HistoryContract.Presenter {

    private var view: HistoryContract.View? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun attachView(view: HistoryContract.View) {
        this.view = view
        loadWeeklyData()
        loadActivityList()
        loadBins()
    }

    override fun detachView() {
        view = null
    }

    override fun onFilterClicked() {
        view?.showError("Filter clicked (not yet implemented)")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onEventTypeSelected(type: String) {
        view?.updateFilterButtons(type)
        val desiredType: EventType? = when (type) {
            "auto" -> EventType.AUTO_OPEN
            "manual" -> EventType.MANUAL_OPEN
            "bin_full" -> EventType.BIN_FULL
            "hold" -> EventType.HOLD_MODE
            "all" -> null
            else -> null
        }

        val activities = getMockActivities().filter { activity ->
            desiredType == null || activity.type == desiredType
        }

        view?.showActivityList(activities)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBinSelected(bin: String) {
        val activities = getMockActivities().filter {
            it.detail.contains(bin, ignoreCase = true)
        }
        view?.showActivityList(activities)
    }

    override fun onDateSelected(date: String) {
        TODO("Not yet implemented")
    }

    override fun loadWeeklyData() {
        val weeklyData = listOf(5, 8, 3, 12, 7, 6, 10)
        view?.showWeeklyData(weeklyData)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun loadActivityData() {
        val dummyActivities = listOf(
            ActivityEvent(
                type = EventType.AUTO_OPEN,
                iconResource = R.drawable.ic_auto_open,
                time = LocalTime.of(8, 30),
                detail = "Detail A",
                description = "Lid opened automatically",
                title = "Auto Open",
                date = LocalDate.now()
            ),
            ActivityEvent(
                type = EventType.MANUAL_OPEN,
                iconResource = R.drawable.ic_manual_open,
                time = LocalTime.of(9, 45),
                detail = "Detail B",
                description = "Lid opened manually",
                title = "Manual Open",
                date = LocalDate.now()
            )
        )
        view?.showActivityList(dummyActivities)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadActivityList() {
        // Show mock activities
        view?.showActivityList(getMockActivities())
    }

    private fun loadBins() {
        val bins = listOf("Bin A", "Bin B", "Bin C")
        view?.setupBinSpinner(bins)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getMockActivities(): List<ActivityEvent> {
        return listOf(
            ActivityEvent(
                type = EventType.AUTO_OPEN,
                iconResource = R.drawable.ic_auto_open,
                time = LocalTime.of(9, 30),
                detail = "Bin A",
                description = "Auto open detected",
                title = "Auto Open",
                date = LocalDate.now()
            ),
            ActivityEvent(
                type = EventType.MANUAL_OPEN,
                iconResource = R.drawable.ic_manual_open,
                time = LocalTime.of(11, 0),
                detail = "Bin B",
                description = "Manual open recorded",
                title = "Manual Open",
                date = LocalDate.now()
            )
        )
    }

}
