package com.eldroid.trashbincloud.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.contract.user.UserContract
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.model.repository.UserRepository
import com.eldroid.trashbincloud.presenter.user.UserPresenter
import com.eldroid.trashbincloud.view.auth.LoginFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.userProfileChangeRequest

class EditProfileActivity : AppCompatActivity(), UserContract.View {

    private lateinit var btnBack: ImageView
    private lateinit var eTextFirstName: TextInputEditText
    private lateinit var eTextLastName: TextInputEditText
    private lateinit var eTextEmail: TextInputEditText
    private lateinit var eTextContactNumber: TextInputEditText
    private lateinit var btnSaveChanges: MaterialButton
    private lateinit var menuChangePassword: LinearLayout

    private lateinit var auth: AuthRepository
    private lateinit var presenter: UserContract.Presenter
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)

        auth = AuthRepository()
        userRepository = UserRepository()
        presenter = UserPresenter(auth, userRepository, this)

        btnBack = findViewById(R.id.btnBack)
        btnSaveChanges = findViewById(R.id.btnSaveChanges)
        eTextFirstName = findViewById(R.id.etFirstName)
        eTextLastName = findViewById(R.id.etLastName)
        eTextEmail = findViewById(R.id.etEmail)
        eTextContactNumber = findViewById(R.id.etContactNumber)
        menuChangePassword = findViewById(R.id.menuChangePassword)

        setupListeners()
        presenter.getUserInfo()
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        menuChangePassword.setOnClickListener {
            startActivity(Intent(this, ChangePassword::class.java))
            finish()
        }

        btnSaveChanges.setOnClickListener {
            val user = auth.currentUser()
            if (user == null) {
                showError("No user logged in.")
                navigateToLogin()
                return@setOnClickListener
            }
            val firstName = eTextFirstName.text.toString().trim()
            val lastName = eTextLastName.text.toString().trim()
            if(lastName.isEmpty()){
                Toast.makeText(this, "Last name is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(firstName.isEmpty()){
                Toast.makeText(this, "First name is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fullName = "$firstName $lastName"
                userRepository.updateUserInfo(user?.uid ?: "", fullName) { success, error ->
                    if (success) {
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, ProfileActivity::class.java))
                        finish()
                    } else {
                        showError(error ?: "Failed to update user info in database.")
                    }
                }
        }
    }

    override fun loadUserInfo(name: String, email: String) {
        val nameParts = name.trim().split(" ")
        val fAndM = (nameParts.size - 2).coerceAtLeast(0)
        val firstName = if (nameParts.isNotEmpty()) nameParts.slice(0..fAndM).joinToString(" ") else ""
        val lastName = if (nameParts.size > 1) nameParts[nameParts.size - 1] else ""

        eTextEmail.setText(email)
        eTextFirstName.setText(firstName)
        eTextLastName.setText(lastName)
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
