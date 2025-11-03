package com.eldroid.trashbincloud.contract.user


interface UserContract {

    interface View{

        fun navigateToLogin()

        fun loadUserInfo(name: String, email: String)

        fun showError(message: String)
    }

    interface Presenter{
        fun getUserInfo()

        fun logout()

    }

}