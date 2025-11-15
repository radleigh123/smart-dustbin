package com.eldroid.trashbincloud.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.contract.MainContract
import com.eldroid.trashbincloud.databinding.ActivityMainBinding
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.presenter.MainPresenter
import com.eldroid.trashbincloud.utils.ThemePreferences
import com.eldroid.trashbincloud.view.auth.AuthActivity
import com.eldroid.trashbincloud.view.settings.SettingsFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), MainContract.View {

    private lateinit var binding: ActivityMainBinding
    private lateinit var presenter: MainContract.Presenter

    private lateinit var navDashboard: LinearLayout
    private lateinit var navHistory: LinearLayout
    private lateinit var navSettings: LinearLayout

    private lateinit var iconDashboard: ImageView
    private lateinit var iconHistory: ImageView
    private lateinit var iconSettings: ImageView

    private lateinit var textDashboard: TextView
    private lateinit var textHistory: TextView
    private lateinit var textSettings: TextView

    private var currentSelectedTab = 0

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "Notifications enabled 🎉", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notifications disabled 🚫", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemePreferences.applyTheme(this)
        super.onCreate(savedInstanceState)

        presenter = MainPresenter(this, AuthRepository())
        presenter.checkAuth()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        initViews()
        setupClickListeners()

        // Default tab: Dashboard
        selectTab(0)
        loadFragment(DashboardFragment())

        if (FirebaseAuth.getInstance().currentUser != null) {
            askNotificationPermissionOnce()
        }
    }

    private fun initViews() {
        navDashboard = findViewById(R.id.nav_dashboard)
        navHistory = findViewById(R.id.nav_history)
        navSettings = findViewById(R.id.nav_settings)

        iconDashboard = findViewById(R.id.icon_dashboard)
        iconHistory = findViewById(R.id.icon_history)
        iconSettings = findViewById(R.id.icon_settings)

        textDashboard = findViewById(R.id.text_dashboard)
        textHistory = findViewById(R.id.text_history)
        textSettings = findViewById(R.id.text_settings)
    }

    private fun setupClickListeners() {
        navDashboard.setOnClickListener {
            if (currentSelectedTab != 0) {
                selectTab(0)
                loadFragment(DashboardFragment())
            }
        }

        navHistory.setOnClickListener {
            if (currentSelectedTab != 1) {
                selectTab(1)
                loadFragment(HistoryFragment())
            }
        }

        navSettings.setOnClickListener {
            if (currentSelectedTab != 2) {
                selectTab(2)
                loadFragment(SettingsFragment())
            }
        }
    }

    private fun selectTab(tabIndex: Int) {
        navDashboard.isSelected = false
        navHistory.isSelected = false
        navSettings.isSelected = false

        currentSelectedTab = tabIndex

        when (tabIndex) {
            0 -> navDashboard.isSelected = true
            1 -> navHistory.isSelected = true
            2 -> navSettings.isSelected = true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun showMessage(message: String) {
        Log.d("MainActivity", message)
        Snackbar.make(findViewById(R.id.fragment_container), message, Snackbar.LENGTH_SHORT).show()
    }

    override fun navigateToLogin() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun askNotificationPermissionOnce() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
            val asked = prefs.getBoolean("notif_permission_asked", false)

            if (!asked) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                prefs.edit().putBoolean("notif_permission_asked", true).apply()
            }
        }
    }
}