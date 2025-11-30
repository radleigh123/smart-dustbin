package com.eldroid.trashbincloud.contract

interface TermsConditionsContract {

    interface View {
        fun showMessage(message: String)
        fun navigateToDashboard()
        fun navigateBack()
    }

    interface Presenter {
        fun onAgreeClicked()
        fun onDeclineClicked()
        fun onBackPressed()
    }
}