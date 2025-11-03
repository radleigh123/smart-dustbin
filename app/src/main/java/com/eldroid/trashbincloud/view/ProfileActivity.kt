package com.eldroid.trashbincloud.view

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.contract.user.UserContract
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.model.repository.UserRepository
import com.eldroid.trashbincloud.presenter.user.UserPresenter
import com.eldroid.trashbincloud.view.auth.AuthActivity
import com.eldroid.trashbincloud.view.auth.LoginFragment

class ProfileActivity : AppCompatActivity(), UserContract.View {
    private lateinit var btnBack: ImageView
    private lateinit var menuEditProfile: LinearLayout
    private lateinit var auth: AuthRepository

    private lateinit var presenter: UserContract.Presenter

    private lateinit var userRepository: UserRepository

    private lateinit var menuLogout: LinearLayout
    private lateinit var menuChangePassword: LinearLayout
    private lateinit var textVName: TextView
    private lateinit var textVEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        auth = AuthRepository()
        userRepository = UserRepository()
        presenter = UserPresenter(auth, userRepository, this)
        presenter.getUserInfo()
        btnBack = findViewById(R.id.btnBack)
        menuEditProfile = findViewById(R.id.menuEditProfile)
        textVName = findViewById(R.id.tvName)
        textVEmail = findViewById(R.id.tvEmail)
        menuLogout = findViewById(R.id.menuLogout)
        menuChangePassword = findViewById(R.id.menuChangePassword)

        setupListeners()

    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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

        menuChangePassword.setOnClickListener {
            startActivity(Intent(this, ChangePassword::class.java))
            finish()
        }

    }

    override fun loadUserInfo(name: String, email: String) {
        textVName.text = name
        textVEmail.text = email
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    override fun navigateToLogin() {
        val intent = Intent(this, LoginFragment::class.java)
        startActivity(intent)
        finish()
    }
}



