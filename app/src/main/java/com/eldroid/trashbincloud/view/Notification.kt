package com.eldroid.trashbincloud.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eldroid.trashbincloud.R
import androidx.activity.OnBackPressedCallback
import com.eldroid.trashbincloud.adapter.NotificationAdapter
import com.eldroid.trashbincloud.contract.NotifContract
import com.eldroid.trashbincloud.model.entity.Notification
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.model.repository.NotifRepository
import com.eldroid.trashbincloud.presenter.notif.NotifPresenter


class Notification : AppCompatActivity(), NotifContract.View {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter
    private lateinit var notif: NotifPresenter
    private lateinit var auth: AuthRepository
    private lateinit var progressBar: ProgressBar

    private lateinit var back: ImageButton
    private var isActive = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        isActive = true
        recyclerView = findViewById(R.id.recyclerViewNotifications)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = NotificationAdapter(emptyList())
        recyclerView.adapter = adapter


        progressBar = findViewById(R.id.progressBar)
        auth = AuthRepository()
        notif = NotifPresenter(this, NotifRepository())
        back = findViewById(R.id.back_btn)

        val userId = auth.currentUser()?.uid
        notif.getNotifications(userId ?: "")
        notif.getUnreadNotif(userId ?: "")

        setupListeners()

    }

    private fun setupListeners() {
        back.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
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
        if (!isActive) return  // Prevent crash if activity is closed
        adapter.updateList(notifications)
    }

    override fun unreadNotifications(unreadCount: Int) {
        if (!isActive) return  // Prevent crash if activity is closed
        val unreadBadge = findViewById<TextView>(R.id.unread)

        if (unreadCount > 0) {
            unreadBadge.text = unreadCount.toString()
            unreadBadge.visibility = View.VISIBLE
        } else {
            unreadBadge.visibility = View.GONE
        }
    }


}
