package com.eldroid.trashbincloud.presenter

import android.content.Context
import com.eldroid.trashbincloud.contract.BinDetailsContract
import com.eldroid.trashbincloud.model.entity.TrashBin

class BinDetailsPresenter(
    private val context: Context
) : BinDetailsContract.Presenter {

    private var view: BinDetailsContract.View? = null

    override fun attachView(view: BinDetailsContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun loadBinData(bin: TrashBin) {
        view?.showBinDetails(bin)
    }
}