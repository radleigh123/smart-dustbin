package com.eldroid.trashbincloud.contract

import com.eldroid.trashbincloud.model.entity.User

interface MainContract {
    interface View {
        fun navigateToLogin()
        fun showMessage(message: String)
    }

    interface Presenter {
        fun checkAuth()
        fun logout()
    }
}