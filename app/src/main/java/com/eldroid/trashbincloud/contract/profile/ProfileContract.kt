package com.eldroid.trashbincloud.contract.profile

import com.eldroid.trashbincloud.model.entity.User

interface ProfileContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showMessage(message: String)
        fun showUserDetails(name: String, email: String)
        fun showProfilePicture()
        fun navigateBack()
    }

    interface Presenter {
        fun getUserDetails()
        fun onBackPressed()
    }
}