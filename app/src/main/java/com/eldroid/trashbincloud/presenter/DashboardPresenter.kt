package com.eldroid.trashbincloud.presenter

import android.util.Log
import com.eldroid.trashbincloud.contract.DashboardContract
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.model.repository.TrashBinRepository

class DashboardPresenter(
    private val view: DashboardContract.View,
    private val authRepository: AuthRepository,
    private val trashBinRepository: TrashBinRepository
): DashboardContract.Presenter {

    override fun getUserInfo() {
        val user = authRepository.currentUser()
        val userUID = user?.uid
        if (user != null) {
            user.let {
                val name = it.displayName.toString()
                val email = it.email.toString()
                val photoUrl = it.photoUrl
                val emailVerified = it.isEmailVerified

                view.loadUserInfo(it.displayName ?: "USER", it.email ?: "EMAIL")
                // Log.d("DashboardPresenter", getUserBins(it.uid).toString())
                getUserBins(it.uid)
            }
        } else {
            view.showError("User not found")
        }
    }

    private fun getUserBins(userUid: String) {
        view.showSkeleton()
        trashBinRepository.getUserBins(userUid) { bins, error ->
            view.hideSkeleton()
            if (error != null) {
                view.showError(error)
            } else if (bins.isEmpty()) {
                view.showNoBins()
            } else {
                view.showBins(bins)
            }
        }
    }

}