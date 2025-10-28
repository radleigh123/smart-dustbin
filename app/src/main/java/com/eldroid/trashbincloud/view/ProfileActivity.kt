package com.eldroid.trashbincloud.view

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.view.auth.AuthActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var menuEditProfile: LinearLayout

    private lateinit var auth: AuthRepository

    private lateinit var menuLogout: LinearLayout
    private lateinit var textVName: TextView
    private lateinit var textVEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
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
    }
}

