package com.eldroid.trashbincloud.presenter.userguide

import com.eldroid.trashbincloud.contract.userguide.UserGuideContract

class UserGuidePresenter(
    private val view: UserGuideContract.View
) : UserGuideContract.Presenter {

    private var expandedStepIndex: Int? = null

    override fun onStepClicked(stepIndex: Int, currentlyExpanded: Boolean) {
        if (currentlyExpanded) {
            // Step is currently expanded, so collapse it
            view.toggleStep(stepIndex, false)
            expandedStepIndex = null
        } else {
            // Step is currently collapsed, so expand it
            // First collapse the previously expanded step
            expandedStepIndex?.let {
                if (it != stepIndex) {
                    view.toggleStep(it, false)
                }
            }

            // Then expand the new step
            view.toggleStep(stepIndex, true)
            expandedStepIndex = stepIndex
        }
    }
    override fun onBackPressed() {
        view.finishActivity()
    }

    override fun onContactSupportClicked() {
        val email = "support@smartbin.com"
        val subject = "SmartBin App Support"
        val body = "Hello,\n\nI need help with the SmartBin system."
        view.openEmailClient(email, subject, body)
    }

    override fun onViewFAQsClicked() {
        view.showMessage("FAQ - Coming soon")
    }
}