package com.eldroid.trashbincloud.presenter.settings

import com.eldroid.trashbincloud.contract.settings.SettingsContract
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.model.repository.UserRepository

class SettingsPresenter(
    private val view: SettingsContract.View,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
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
                } else view.showMessage(message ?: "SETTINGS: User info retrieval error")
            }
        }
    }

    override fun logout() {
        authRepository.logout()
        view.navigateToLogin()
    }

}