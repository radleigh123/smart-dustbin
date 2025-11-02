package com.eldroid.trashbincloud.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.view.profile.ProfileActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class ChangePassword : AppCompatActivity() {

    private lateinit var btnSaveChanges: MaterialButton
    private lateinit var eTextNewPassword: TextInputEditText
    private lateinit var eTextConfirmNewPassword: TextInputEditText
    private lateinit var auth: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_change_password)

        auth = AuthRepository()

        btnSaveChanges = findViewById(R.id.btnSave)
        eTextNewPassword = findViewById(R.id.etNewPassword)
        eTextConfirmNewPassword = findViewById(R.id.etConfirmNewPassword)

        setupListeners()
    }

    private fun setupListeners() {
        btnSaveChanges.setOnClickListener {
            val currentUser = auth.currentUser()
            val newPassword = eTextNewPassword.text.toString().trim()
            val confirmNewPassword = eTextConfirmNewPassword.text.toString().trim()

            if(newPassword.isEmpty()){
                Toast.makeText(this, "Please input your new password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(confirmNewPassword.isEmpty()){
                Toast.makeText(this, "Please input your confirm new password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(newPassword != confirmNewPassword){
                Toast.makeText(this, "New password and confirm new password do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }
            if(newPassword.length < 8) {
                Toast.makeText(this, "New password must be at least 8 characters long", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(!newPassword.matches(".*[A-Z].*".toRegex())) {
                Toast.makeText(this, "New password must contain at least one uppercase letter", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(!newPassword.matches(".*[*!@#$%^&()_+].*".toRegex())) {
                Toast.makeText(this, "New password must contain at least one symbol", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(!newPassword.matches(".*[0-9].*".toRegex())) {
                Toast.makeText(this, "New password must contain at least one number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            currentUser?.reload()?.addOnCompleteListener { reloadTask ->
                if (reloadTask.isSuccessful) {
                    currentUser.updatePassword(newPassword)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "You have successfully updated your password", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, ProfileActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Failed to refresh user session. Please log in again.", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }
}