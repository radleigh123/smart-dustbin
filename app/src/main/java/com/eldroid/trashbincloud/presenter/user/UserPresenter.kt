package com.eldroid.trashbincloud.presenter.user

import com.eldroid.trashbincloud.contract.user.UserContract
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.model.repository.UserRepository

class UserPresenter(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val view: UserContract.View
) : UserContract.Presenter {

    override fun getUserInfo() {
        val currentUser = authRepository.currentUser()

        if (currentUser == null) {
            view.showError("User not logged in.")
            return
        }

        val uid = currentUser.uid

        // Try to load user info from the Realtime Database first
        userRepository.getUserInfo(uid) { user, error ->
            if (error != null) {
                view.showError(error)
                return@getUserInfo
            }

            if (user != null) {
                view.loadUserInfo(user.displayName ?: "No Name", user.email ?: "")
            } else {
                // fallback to Firebase Auth data if user record not found
                val name = currentUser.displayName ?: "No Name"
                val email = currentUser.email ?: "No Email"
                view.loadUserInfo(name, email)
            }
        }
    }

    override fun logout() {
        authRepository.logout()
        view.navigateToLogin()
    }
}
