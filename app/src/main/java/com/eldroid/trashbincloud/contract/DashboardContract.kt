package com.eldroid.trashbincloud.contract

import com.eldroid.trashbincloud.model.entity.TrashBin

interface DashboardContract {
    interface View {
        fun showSkeleton()
        fun hideSkeleton()
        fun showError(message: String)
        fun loadUserInfo(name: String, email: String)
        fun showBins(bins: List<TrashBin>)
        fun showNoBins()
    }

    interface Presenter {
        fun getUserInfo()
    }
}