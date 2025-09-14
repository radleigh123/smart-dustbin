package com.eldroid.trashbincloud.contract

interface MainContract {
    interface View {
        fun showLogoutSuccess()
        fun navigateToLogin()
    }

    interface Presenter {
        fun logout()
    }
}