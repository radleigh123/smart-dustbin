package com.eldroid.trashbincloud.contract

import com.eldroid.trashbincloud.model.entity.Notification

interface NotifContract {
    interface View {
        fun showNotifications(notifications: List<Notification>)

        fun unreadNotifications(unreadCount: Int)

        fun showLoading()

        fun hideLoading()

        fun showError(message: String)
    }

    interface Presenter {
        fun getNotifications(userId: String)

        fun getUnreadNotif(userId: String)
    }
}