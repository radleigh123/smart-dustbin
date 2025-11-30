package com.eldroid.trashbincloud.presenter

import com.eldroid.trashbincloud.contract.FaqContract

class FaqPresenter(
    private val view: FaqContract.View
) : FaqContract.Presenter {

    override fun onContactSupportClicked() {
        view.navigateToContactSupport()
    }

    override fun onBackPressed() {
        view.navigateBack()
    }
}