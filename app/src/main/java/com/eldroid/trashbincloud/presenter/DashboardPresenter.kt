package com.eldroid.trashbincloud.presenter

import com.eldroid.trashbincloud.contract.DashboardContract
import com.eldroid.trashbincloud.model.repository.AuthRepository

class DashboardPresenter(
    private val view: DashboardContract.View,
    private val authRepository: AuthRepository
): DashboardContract.Presenter {

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

}