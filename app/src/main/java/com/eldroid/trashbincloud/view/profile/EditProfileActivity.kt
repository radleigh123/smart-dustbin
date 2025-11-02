package com.eldroid.trashbincloud.view.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.view.ChangePassword
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.userProfileChangeRequest

class EditProfileActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var eTextFirstName: TextInputEditText
    private lateinit var eTextLastName: TextInputEditText
    private lateinit var eTextEmail: TextInputEditText
    private lateinit var eTextContactNumber: TextInputEditText

    private lateinit var btnSaveChanges: MaterialButton

    private lateinit var menuChangePassword: LinearLayout
    private lateinit var auth: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)
        auth = AuthRepository()

        btnBack = findViewById(R.id.btnBack)
        btnSaveChanges = findViewById(R.id.btnSaveChanges)
        eTextFirstName = findViewById(R.id.etFirstName)
        eTextLastName = findViewById(R.id.etLastName)
        menuChangePassword = findViewById(R.id.menuChangePassword)
        eTextEmail = findViewById(R.id.etEmail)
        eTextContactNumber = findViewById(R.id.etContactNumber)


        val displayName = auth.currentUser()?.displayName ?: ""
        val email = auth.currentUser()?.email ?: ""
        val nameParts = displayName.trim().split(" ")
        val fAndM = (nameParts.size - 2).coerceAtLeast(0)
        val firstName = if (nameParts.isNotEmpty()) nameParts.slice(0..fAndM).joinToString(" ") else ""
        val lastName = if (nameParts.size > 1) nameParts[nameParts.size - 1] else ""
        eTextEmail.setText(email)
        eTextFirstName.setText(firstName)
        eTextLastName.setText(lastName)

        setupListeners()
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        menuChangePassword.setOnClickListener {
            startActivity(Intent(this, ChangePassword::class.java))
            finish()
        }

        btnSaveChanges.setOnClickListener {
            val user = auth.currentUser()
            val firstName = eTextFirstName.text.toString().trim()
            val lastName = eTextLastName.text.toString().trim()

            if (user == null) {
                Log.e("EditProfile", "No user logged in")
                return@setOnClickListener
            }

            // app logic should be in presenters
            /*val profileUpdates = userProfileChangeRequest {
                UserProfileChangeRequest.Builder.setDisplayName = "$firstName $lastName"
            }

            user.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("EditProfile", "User profile updated successfully")
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, ProfileActivity::class.java))
                    } else {
                        Log.e("EditProfile", "Failed to update profile", task.exception)
                        Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()

                    }
                }*/
        }
    }
}