package com.eldroid.trashbincloud.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.adapter.NotificationAdapter
import com.eldroid.trashbincloud.contract.NotifContract
import com.eldroid.trashbincloud.model.entity.Notification
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.model.repository.NotifRepository
import com.eldroid.trashbincloud.presenter.notif.NotifPresenter
import com.eldroid.trashbincloud.utils.ThemePreferences

class Notification : AppCompatActivity(), NotifContract.View {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter
    private lateinit var notif: NotifPresenter

    private lateinit var emptyState: TextView
    private lateinit var auth: AuthRepository
    private lateinit var progressBar: ProgressBar
    private lateinit var back: ImageButton
    private lateinit var moreBtn: ImageButton
    private var isActive = false
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemePreferences.applyTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        isActive = true

        recyclerView = findViewById(R.id.recyclerViewNotifications)
        recyclerView.layoutManager = LinearLayoutManager(this)

        auth = AuthRepository()
        notif = NotifPresenter(this, NotifRepository())
        progressBar = findViewById(R.id.progressBar)
        emptyState = findViewById(R.id.emptyState)
        back = findViewById(R.id.back_btn)
        moreBtn = findViewById(R.id.more_btn)
        userId = auth.currentUser()?.uid

        adapter = NotificationAdapter(emptyList()) { notifItem ->
            userId?.let { uid ->
                notif.markAsRead(uid, notifItem.notifId ?: "")
                notif.getUnreadNotif(uid) // refresh badge
            }
        }

        recyclerView.adapter = adapter

        userId?.let {
            notif.getNotifications(it)
            notif.getUnreadNotif(it)
        }

        back.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Setup dropdown menu for more button
        moreBtn.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }

    private fun showPopupMenu(view: View) {
        val themeResId = if (ThemePreferences.isDarkModeEnabled(this)) {
            R.style.DarkCustomPopupMenu
        } else {
            R.style.CustomPopupMenu
        }

        val wrapper = android.view.ContextThemeWrapper(this, themeResId)
        val popup = PopupMenu(wrapper, view)

        popup.menuInflater.inflate(R.menu.notification_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_mark_all_read -> {
                    userId?.let { uid ->
                        notif.markAllAsRead(uid)
                    }
                    true
                }
                R.id.menu_mark_all_unread -> {
                    userId?.let { uid ->
                        notif.markAllAsUnread(uid)
                    }
                    true
                }
                else -> false
            }
        }

        popup.show()
    }


    override fun onDestroy() {
        super.onDestroy()
        isActive = false
    }

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showNotifications(notifications: List<Notification>) {
        if (!isActive) return

        if (notifications.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyState.visibility = View.GONE
        }

        adapter.updateList(notifications)
    }


    override fun unreadNotifications(unreadCount: Int) {
        if (!isActive) return
        val unreadBadge = findViewById<TextView>(R.id.unread)
        if (unreadCount > 0) {
            unreadBadge.text = unreadCount.toString()
            unreadBadge.visibility = View.VISIBLE
        } else {
            unreadBadge.visibility = View.GONE
        }
    }

    override fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}