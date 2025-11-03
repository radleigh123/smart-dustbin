package com.eldroid.trashbincloud.presenter.notif

import com.eldroid.trashbincloud.contract.NotifContract
import com.eldroid.trashbincloud.model.repository.NotifRepository

class NotifPresenter(
    private val view: NotifContract.View,
    private val repository: NotifRepository
) : NotifContract.Presenter {

    override fun getNotifications(userId: String) {
        view.showLoading()
        repository.getNotifications(userId) { notifications, error ->
            view.hideLoading()
            if (error != null) {
                view.showError(error)
            } else {
                view.showNotifications(notifications)
            }
        }
    }

    override fun getUnreadNotif(userId: String) {
        repository.getUnreadNotif(userId) { unreadCount, error ->
            if (error != null) {
                view.showError(error)
            } else {
                view.unreadNotifications(unreadCount)
            }
        }
    }

    fun markAsRead(userId: String, notifId: String) {
        repository.markAsRead(userId, notifId)
    }
}
