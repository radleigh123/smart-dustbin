package com.eldroid.trashbincloud.view

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.eldroid.trashbincloud.R

class ProfileActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var menuEditProfile: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        btnBack = findViewById(R.id.btnBack)
        menuEditProfile = findViewById(R.id.menuEditProfile)

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
    }
}