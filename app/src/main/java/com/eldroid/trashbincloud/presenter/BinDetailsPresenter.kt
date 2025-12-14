package com.eldroid.trashbincloud.presenter

import android.content.Context
import com.eldroid.trashbincloud.contract.BinDetailsContract
import com.eldroid.trashbincloud.model.entity.TrashBin
import com.eldroid.trashbincloud.model.repository.ActivityRepository
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.model.repository.TrashBinRepository
import com.eldroid.trashbincloud.model.repository.UserRepository

class BinDetailsPresenter(
    private val context: Context,
    private val authRepo: AuthRepository,
    private val actRepo: ActivityRepository
) : BinDetailsContract.Presenter {

    private var view: BinDetailsContract.View? = null

    override fun attachView(view: BinDetailsContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun loadBinData(bin: TrashBin) {
        val userUid = authRepo.currentUserId()
        view?.showBinDetails(bin)

        actRepo.getUserBins(userUid!!, bin.binId!!) { activities, error ->
            if (error != null) {
                view?.showError(error)
            } else if (activities.isNotEmpty()) {
                view?.showActivities(activities)
            } else {
                view?.showNoActivities(activities)
            }
        }
    }
}