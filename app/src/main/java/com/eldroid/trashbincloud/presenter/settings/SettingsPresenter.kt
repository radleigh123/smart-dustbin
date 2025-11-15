package com.eldroid.trashbincloud.presenter.settings

import android.content.Context
import com.eldroid.trashbincloud.contract.settings.SettingsContract
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.model.repository.UserRepository
import com.eldroid.trashbincloud.utils.ThemePreferences

class SettingsPresenter(
    private val view: SettingsContract.View,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val context: Context
): SettingsContract.Presenter {

    override fun getUserInfo() {
        val uid = authRepository.currentUserId()

        uid?.let {
            userRepository.getUser(it) { user, message ->
                if (user != null) {
                    val name = user.name ?: return@getUser
                    val email = user.email ?: return@getUser
                    val contactNumber = user.contactNumber ?: return@getUser
                    view.loadUserInfo(name, email, contactNumber)
                } else {
                    view.showMessage(message ?: "SETTINGS: User info retrieval error")
                }
            }
        }
    }

    override fun logout() {
        authRepository.logout()
        view.navigateToLogin()
    }

    override fun loadThemePreference() {
        val isDarkMode = ThemePreferences.isDarkModeEnabled(context)
        view.updateThemeSwitch(isDarkMode)
    }

    override fun toggleTheme(enabled: Boolean) {
        ThemePreferences.setDarkMode(context, enabled)
    }
}