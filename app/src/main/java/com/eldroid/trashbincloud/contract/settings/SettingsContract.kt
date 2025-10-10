package com.eldroid.trashbincloud.contract.settings

interface SettingsContract {
    interface View {
        fun navigateToLogin()
        fun showError(message: String)
        fun loadUserInfo(name: String, email: String)
    }

    interface Presenter {
        fun getUserInfo()
        fun logout()
    }
}