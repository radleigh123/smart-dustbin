package com.eldroid.trashbincloud.contract.userguide

interface UserGuideContract {
    interface View {
        fun showMessage(message: String)
        fun toggleStep(stepIndex: Int, isExpanded: Boolean)
        fun openEmailClient(email: String, subject: String, body: String)
        fun finishActivity()
    }

    interface Presenter {
        fun onStepClicked(stepIndex: Int, currentlyExpanded: Boolean)
        fun onBackPressed()
        fun onContactSupportClicked()
        fun onViewFAQsClicked()
    }
}
