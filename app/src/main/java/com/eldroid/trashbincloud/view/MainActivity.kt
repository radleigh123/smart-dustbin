package com.eldroid.trashbincloud.view

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.contract.MainContract
import com.eldroid.trashbincloud.databinding.ActivityMainBinding
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.presenter.MainPresenter
import com.eldroid.trashbincloud.view.auth.IndexActivity

class MainActivity : AppCompatActivity(), MainContract.View {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = MainPresenter(this, AuthRepository())
        presenter.checkUser()

        setContentView(R.layout.activity_main)

        initViews()
        setupClickListeners()

        // Default tab: Dashboard
        selectTab(0)
        loadFragment(DashboardFragment())
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
        resetAllTabs()
        currentSelectedTab = tabIndex

        val selectedColor = ContextCompat.getColor(this, R.color.teal_700)

        when (tabIndex) {
            0 -> {
                navDashboard.isSelected = true
                iconDashboard.setColorFilter(selectedColor)
                textDashboard.setTextColor(selectedColor)
            }
            1 -> {
                navHistory.isSelected = true
                iconHistory.setColorFilter(selectedColor)
                textHistory.setTextColor(selectedColor)
            }
            2 -> {
                navSettings.isSelected = true
                iconSettings.setColorFilter(selectedColor)
                textSettings.setTextColor(selectedColor)
            }
        }
    }
    private fun resetAllTabs() {
        val unselectedColor = ContextCompat.getColor(this, R.color.gray_500)

        navDashboard.isSelected = false
        navHistory.isSelected = false
        navSettings.isSelected = false

        iconDashboard.setColorFilter(unselectedColor)
        iconHistory.setColorFilter(unselectedColor)
        iconSettings.setColorFilter(unselectedColor)

        textDashboard.setTextColor(unselectedColor)
        textHistory.setTextColor(unselectedColor)
        textSettings.setTextColor(unselectedColor)
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // ---------------- MVP Contract ----------------
    override fun showLogoutSuccess() {
        Snackbar.make(findViewById(R.id.fragment_container), "Logged out successfully", Snackbar.LENGTH_SHORT).show()
    }

    override fun navigateToLogin() {
        val intent = Intent(this, IndexActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
// Example Fragments - *Had to comment, causing a redeclaration error
/*
class DashboardFragment : Fragment(R.layout.fragment_main_dashboard)
class HistoryFragment : Fragment(R.layout.fragment_history)
class SettingsFragment : Fragment(R.layout.fragment_settings)
*/
