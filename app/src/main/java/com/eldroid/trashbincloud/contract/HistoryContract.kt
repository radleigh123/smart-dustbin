package com.eldroid.trashbincloud.contract

import com.eldroid.trashbincloud.model.entity.ActivityEvent
import java.time.LocalDate

interface HistoryContract {
    interface View {
        fun showWeeklyData(data: List<Int>)
        fun showActivityList(activities: List<ActivityEvent>)
        fun updateFilterButtons(selectedFilter: String)
        fun setupBinSpinner(bins: List<String>)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun toggleFilterSection(show: Boolean) // Add this
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun onFilterClicked()
        fun onEventTypeSelected(type: String)
        fun onBinSelected(binName: String)
        fun onDateSelected(date: LocalDate?)
        fun loadWeeklyData()
        fun loadActivityData()
    }
}