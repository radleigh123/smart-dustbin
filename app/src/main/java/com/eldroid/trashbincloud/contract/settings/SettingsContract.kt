package com.eldroid.trashbincloud.contract.settings

interface SettingsContract {
    interface View {
        fun navigateToLogin()
        fun showMessage(message: String)
        fun loadUserInfo(name: String, email: String, contactNumber: String)
        fun updateThemeSwitch(isDarkMode: Boolean)
    }

    interface Presenter {
        fun getUserInfo()
        fun logout()
        fun loadThemePreference()
        fun toggleTheme(enabled: Boolean)
    }
}