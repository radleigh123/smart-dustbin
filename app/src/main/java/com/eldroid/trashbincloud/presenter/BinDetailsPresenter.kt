package com.eldroid.trashbincloud.presenter

import com.eldroid.trashbincloud.contract.BinDetailsContract
import com.eldroid.trashbincloud.model.entity.TrashBin

class BinDetailsPresenter : BinDetailsContract.Presenter {

    private var view: BinDetailsContract.View? = null

    override fun attachView(view: BinDetailsContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun loadBinData() {
        // Dummy data
        val dummyBin = TrashBin(
            binId = "BIN123",
            name = "Kitchen Bin",
            location = "Main Kitchen, Floor 2",
            fillLevel = 80,
            status = 0,
            lastUpdated = System.currentTimeMillis(),
            battery = 75,
            temperature = 24.5f,
            lastEmptied = "Sep 18",
            daysToFill = 2.5
        )

        view?.showBinDetails(dummyBin)
    }
}