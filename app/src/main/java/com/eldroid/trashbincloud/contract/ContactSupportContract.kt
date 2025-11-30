package com.eldroid.trashbincloud.contract

interface ContactSupportContract {

    interface View {
        fun openEmailClient(email: String)
        fun openPhoneDialer(phone: String)
        fun showError(message: String)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun onEmailContactClicked(email: String)
        fun onPhoneContactClicked(phone: String)
    }
}