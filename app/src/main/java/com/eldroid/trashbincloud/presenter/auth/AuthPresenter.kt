package com.eldroid.trashbincloud.presenter.auth

import com.eldroid.trashbincloud.contract.auth.AuthContract
import com.eldroid.trashbincloud.model.repository.AuthRepository

class AuthPresenter(
    private val view: AuthContract.View,
    private val repository: AuthRepository
) : AuthContract.Presenter {
    override fun login(email: String, password: String) {
        view.showLoading()
        repository.login(email, password) { success, error ->
            view.hideLoading()
            if (success) {
                view.navigate()
            } else {
                view.showError(error ?: "Unknown error")
            }
        }
    }

    override fun register(email: String, password: String) {
        view.showLoading()
        repository.register(email, password) { success, error ->
            view.hideLoading()
            if (success) {
                view.navigate()
            } else {
                view.showError(error ?: "Unknown error")
            }
        }
    }

    override fun sendResetPasswordEmail(email: String) {
        repository.sendResetPasswordEmail(email) { success, error ->
            if (success) {
                view.showError("Password reset email sent.")
                view.navigate()
            } else {
                view.showError(error ?: "Unknown error")
            }
        }
    }
}