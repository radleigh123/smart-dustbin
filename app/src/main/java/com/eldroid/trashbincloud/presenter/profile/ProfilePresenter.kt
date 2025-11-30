package com.eldroid.trashbincloud.presenter.profile

import com.eldroid.trashbincloud.contract.auth.AuthContract
import com.eldroid.trashbincloud.contract.profile.ProfileContract
import com.eldroid.trashbincloud.model.entity.User
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.model.repository.UserRepository

class ProfilePresenter(
    private val view: ProfileContract.View,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ProfileContract.Presenter {

    override fun getUserDetails() {
        val uid = authRepository.currentUserId()
        uid?.let {
            userRepository.getUser(it) { user, message ->
                if (user != null) {
                    val name = user.name ?: ""
                    val email = user.email ?: ""
                    val contactNumber = user.contactNumber ?: ""

                    view.showUserDetails(name, email, contactNumber)
                } else {
                    view.showMessage(message ?: "PROFILE: User info retrieval error")
                }
            }
        }
    }

    override fun onBackPressed() {
        view.navigateBack()
    }
}