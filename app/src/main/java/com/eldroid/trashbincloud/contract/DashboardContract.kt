package com.eldroid.trashbincloud.contract

interface DashboardContract {
    interface View {
        fun showSkeleton()
        fun hideSkeleton()
        fun showError(message: String)
        fun loadUserInfo(name: String, email: String)
    }

    interface Presenter {
        fun getUserInfo()
    }
}