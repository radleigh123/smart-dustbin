package com.eldroid.trashbincloud.contract

import com.eldroid.trashbincloud.model.entity.TrashBin
import com.eldroid.trashbincloud.model.entity.User

interface DashboardContract {
    interface View {
        fun showSkeleton()
        fun hideSkeleton()
        fun showMessage(message: String)
        fun loadUserInfo(user: User)
        fun showBins(bins: List<TrashBin>)
        fun showNoBins()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun getUserInfo()
        fun updateBinCommand(bin: TrashBin, cmd: String)
    }
}