package com.eldroid.trashbincloud.contract.auth

interface AuthContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun navigate()
    }

    interface Presenter {
        fun login(email: String, password: String)
        fun register(email: String, password: String, name: String, contactNumber: String)
        fun sendResetPasswordEmail(email: String)
    }
}