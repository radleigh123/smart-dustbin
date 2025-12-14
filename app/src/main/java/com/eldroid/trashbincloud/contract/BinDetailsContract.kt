package com.eldroid.trashbincloud.contract

import com.eldroid.trashbincloud.model.entity.ActivityEvent
import com.eldroid.trashbincloud.model.entity.TrashBin

interface BinDetailsContract {

    interface View {
        fun showBinDetails(bin: TrashBin)
        fun showError(message: String)
        fun findViewById(
            statusDot: Int,
            tvActivityTitle: Int,
            tvActivityTime: Int,
            ivInfo: Int
        ): View

        fun showActivities(activities: List<ActivityEvent>)
        fun showNoActivities(activities: List<ActivityEvent>)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadBinData(bin: TrashBin)
    }
}