package com.eldroid.trashbincloud.presenter

import com.eldroid.trashbincloud.contract.ContactSupportContract

class ContactSupportPresenter : ContactSupportContract.Presenter {

    private var view: ContactSupportContract.View? = null

    override fun attachView(view: ContactSupportContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun onEmailContactClicked(email: String) {
        if (email.isNotEmpty()) {
            view?.openEmailClient(email)
        } else {
            view?.showError("Email address not available")
        }
    }

    override fun onPhoneContactClicked(phone: String) {
        if (phone.isNotEmpty()) {
            view?.openPhoneDialer(phone)
        } else {
            view?.showError("Phone number not available")
        }
    }
}