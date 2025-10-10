package com.eldroid.trashbincloud.presenter.settings

import com.eldroid.trashbincloud.contract.settings.SettingsContract
import com.eldroid.trashbincloud.model.repository.AuthRepository

class SettingsPresenter(
    private val view: SettingsContract.View,
    private val authRepository: AuthRepository
): SettingsContract.Presenter {

    override fun getUserInfo() {
        val user = authRepository.currentUser()
        if (user != null) {
            user.let {
                val name = it.displayName.toString()
                val email = it.email.toString()
                val photoUrl = it.photoUrl

                val emailVerified = it.isEmailVerified

                view.loadUserInfo(name, email)
            }
        } else {
            view.showError("User not found")
        }
    }

    override fun logout() {
        authRepository.logout()
        view.navigateToLogin()
    }

}