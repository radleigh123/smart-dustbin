package com.eldroid.trashbincloud.presenter

import com.eldroid.trashbincloud.contract.MainContract
import com.eldroid.trashbincloud.model.repository.AuthRepository

class MainPresenter(
    private val view: MainContract.View,
    private val authRepository: AuthRepository
): MainContract.Presenter {

    override fun checkAuth() {
        val uid = authRepository.currentUserId()
        if (uid == null) {
            view.navigateToLogin()
        }
    }

    override fun logout() {
        authRepository.logout()
        view.showMessage("Logged out successfully")
        view.navigateToLogin()
    }

}