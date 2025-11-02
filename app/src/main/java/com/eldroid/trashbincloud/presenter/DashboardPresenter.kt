package com.eldroid.trashbincloud.presenter

import android.util.Log
import com.eldroid.trashbincloud.contract.DashboardContract
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.model.repository.TrashBinRepository
import com.eldroid.trashbincloud.model.repository.UserRepository

class DashboardPresenter(
    private var view: DashboardContract.View?,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val trashBinRepository: TrashBinRepository
): DashboardContract.Presenter {
    override fun attachView(view: DashboardContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun getUserInfo() {
        val uid = authRepository.currentUserId()

        uid?.let {
            userRepository.getUser(it) { user, message ->
                if (this.view === null) return@getUser
                if (user != null) {
                    view?.loadUserInfo(user)
                    getUserBins(uid)
                } else view?.showMessage(message ?: "User info retrieval error")
            }
        }
    }

    private fun getUserBins(userUid: String) {
        view?.showSkeleton()
        trashBinRepository.getUserBins(userUid) { bins, error ->
            if (this.view === null) return@getUserBins
            view?.hideSkeleton()
            if (error != null) {
                view?.showMessage(error)
            } else if (bins.isEmpty()) {
                view?.showNoBins()
            } else {
                view?.showBins(bins)
            }
        }
    }

}