package com.eldroid.trashbincloud.view.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.contract.MainContract
import com.eldroid.trashbincloud.contract.profile.ProfileContract
import com.eldroid.trashbincloud.databinding.ActivityProfileBinding
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.model.repository.UserRepository
import com.eldroid.trashbincloud.presenter.profile.ProfilePresenter
import com.eldroid.trashbincloud.view.MainActivity
import com.eldroid.trashbincloud.view.auth.AuthActivity
import com.eldroid.trashbincloud.view.settings.SettingsFragment
import com.google.android.material.snackbar.Snackbar

class ProfileActivity : AppCompatActivity(), ProfileContract.View {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var presenter: ProfileContract.Presenter

    private lateinit var btnBack: ImageView
    private lateinit var menuEditProfile: LinearLayout
    private lateinit var auth: AuthRepository
    private lateinit var menuLogout: LinearLayout
    private lateinit var textVName: TextView
    private lateinit var textVEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = ProfilePresenter(this, AuthRepository(), UserRepository())
        presenter.getUserDetails()

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setContentView(R.layout.activity_profile)
        auth = AuthRepository()
        btnBack = findViewById(R.id.btnBack)
        menuEditProfile = findViewById(R.id.menuEditProfile)

        textVName = findViewById(R.id.tvName)
        textVEmail = findViewById(R.id.tvEmail)

        menuLogout = findViewById(R.id.menuLogout)

        val username = auth.currentUser()?.displayName
        val email = auth.currentUser()?.email

        textVName.text = username ?: ""
        textVEmail.text = email ?: ""

        setupListeners()

    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            navigateBack()
        }
        menuEditProfile.setOnClickListener {
            startActivity(
                Intent(this, EditProfileActivity::class.java)
            )
        }

        menuLogout.setOnClickListener {
            auth.logout()
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }

    override fun showLoading() {
        Log.d("ProfileActivity", "showLoading not yet implemented")
    }

    override fun hideLoading() {
        Log.d("ProfileActivity", "hideLoading not yet implemented")
    }

    override fun showMessage(message: String) {
        Log.d("ProfileActivity", message)
    }

    override fun showUserDetails(name: String, email: String) {
        binding.tvName.text = name
        binding.tvEmail.text = email
    }

    override fun showProfilePicture() {
        Log.d("ProfileActivity", "Profile picture not yet implemented")
    }

    override fun navigateBack() {
        /*val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("fragment", "SettingsFragment")
        startActivity(intent)
        finish()*/
        onBackPressedDispatcher.onBackPressed()
    }
}