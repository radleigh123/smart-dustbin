package com.eldroid.trashbincloud.presenter

import android.util.Log
import com.eldroid.trashbincloud.contract.DashboardContract
import com.eldroid.trashbincloud.model.entity.Commands
import com.eldroid.trashbincloud.model.entity.TrashBin
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
//                    getUserBins(uid)
                    trashBinRepository.getUserBins(uid) { bins, error ->
                        if (bins.isNotEmpty()) view?.showBins(bins)
                        else view?.showNoBins()
                    }
                } else view?.showMessage(message ?: "User info retrieval error")
            }
        }
    }

    override fun updateBinCommand(bin: TrashBin, cmd: String) {
        val userUid = authRepository.currentUserId().toString()
        if (cmd == "open" || cmd == "close") {
            bin.commands?.command = cmd
            bin.commands?.mode = "manual"
        } else {
            bin.commands?.command = "auto"
            bin.commands?.mode = "auto"
        }

        trashBinRepository.updateBinCommand(userUid, bin.binId ?: "", bin.commands ?: Commands()) { success, error ->
            if (success) view?.showMessage("Bin ${bin.name} commands changed")
            else view?.showMessage(error ?: "Command failed")
        }
    }

}