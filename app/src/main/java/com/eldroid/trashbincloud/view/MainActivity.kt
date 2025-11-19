package com.eldroid.trashbincloud.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.contract.MainContract
import com.eldroid.trashbincloud.databinding.ActivityMainBinding
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.presenter.MainPresenter
import com.eldroid.trashbincloud.utils.ThemePreferences
import com.eldroid.trashbincloud.view.auth.AuthActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), MainContract.View {

    private lateinit var binding: ActivityMainBinding
    private lateinit var presenter: MainContract.Presenter
    private lateinit var navController: NavController

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
        setContentView(binding.root)

        navController = findNavController(R.id.nav_host_fragment_content_main)

        setupClickListeners();

        if (FirebaseAuth.getInstance().currentUser != null) {
            askNotificationPermissionOnce()
        }
    }

    private fun setupClickListeners() {
        binding.root.findViewById<LinearLayout>(R.id.nav_dashboard).setOnClickListener {
            navController.navigate(R.id.DashboardFragment)
        }
        binding.root.findViewById<LinearLayout>(R.id.nav_history).setOnClickListener {
            navController.navigate(R.id.HistoryFragment)
        }
        binding.root.findViewById<LinearLayout>(R.id.nav_settings).setOnClickListener {
            navController.navigate(R.id.SettingsFragment)
        }
    }

    override fun showMessage(message: String) {
        Log.d("MainActivity", message)
        Snackbar.make(findViewById(R.id.nav_host_fragment_content_main), message, Snackbar.LENGTH_SHORT).show()
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