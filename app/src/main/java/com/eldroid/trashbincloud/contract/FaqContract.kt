package com.eldroid.trashbincloud.contract

interface FaqContract {

    interface View {
        fun navigateToContactSupport()
        fun navigateBack()
    }

    interface Presenter {
        fun onContactSupportClicked()
        fun onBackPressed()
    }
}
