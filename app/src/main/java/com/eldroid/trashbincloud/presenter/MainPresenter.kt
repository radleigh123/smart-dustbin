package com.eldroid.trashbincloud.presenter

import com.eldroid.trashbincloud.contract.MainContract
import com.eldroid.trashbincloud.model.repository.AuthRepository

class MainPresenter(
    private val view: MainContract.View,
    private val repository: AuthRepository
): MainContract.Presenter {

    override fun checkUser() {
        val user = repository.currentUser()
        if (user == null) {
            view.navigateToLogin()
        }
    }

    override fun logout() {
        repository.logout()
        view.showLogoutSuccess()
        view.navigateToLogin()
    }

}