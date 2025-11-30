package com.eldroid.trashbincloud.presenter

import com.eldroid.trashbincloud.contract.TermsConditionsContract

class TermsConditionsPresenter(
    private val view: TermsConditionsContract.View
) : TermsConditionsContract.Presenter {

    override fun onAgreeClicked() {
        view.showMessage("Thank you for accepting our terms")
        view.navigateToDashboard()
    }

    override fun onDeclineClicked() {
        view.showMessage("Terms & Conditions declined")
        view.navigateBack()
    }

    override fun onBackPressed() {
        view.navigateBack()
    }
}