package com.eldroid.trashbincloud.presenter.auth

import com.eldroid.trashbincloud.contract.auth.AuthContract
import com.eldroid.trashbincloud.model.entity.User
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.model.repository.UserRepository

class AuthPresenter(
    private val view: AuthContract.View,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : AuthContract.Presenter {

    override fun login(email: String, password: String) {
        view.showLoading()
        authRepository.login(email, password) { success, error ->
            view.hideLoading()
            if (success) {
                view.navigate()
            } else {
                view.showError(error ?: "Unknown error")
            }
        }
    }

    override fun register(
        email: String,
        password: String,
        name: String,
        contactNumber: String
    ) {
        view.showLoading()
        authRepository.register(email, password) { success, error ->
            view.hideLoading()
            if (success) {
                val uid = authRepository.currentUserId() ?: return@register

                val now = System.currentTimeMillis()

                val user = User(
                    name = name,
                    email = email,
                    role = "user",
                    contactNumber = contactNumber,
                    createdAt = now,
                    lastUpdated = now
                )
                userRepository.addUser(uid, user) { saved, _ ->
                    if (saved) {
                        view.navigate()
                    } else {
                        view.showError("RTDB: Failed to add user profile")
                    }
                }
            } else {
                view.showError(error ?: "Unknown error")
            }
        }
    }

    override fun sendResetPasswordEmail(email: String) {
        authRepository.sendResetPasswordEmail(email) { success, error ->
            if (success) {
                view.showError("Password reset email sent.")
                view.navigate()
            } else {
                view.showError(error ?: "Unknown error")
            }
        }
    }
}