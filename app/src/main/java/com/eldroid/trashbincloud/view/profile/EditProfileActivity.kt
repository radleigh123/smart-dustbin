package com.eldroid.trashbincloud.view.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.contract.profile.ProfileContract
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.model.repository.UserRepository
import com.eldroid.trashbincloud.presenter.profile.ProfilePresenter
import com.eldroid.trashbincloud.utils.ThemePreferences
import com.eldroid.trashbincloud.view.ChangePassword
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class EditProfileActivity : AppCompatActivity(), ProfileContract.View {

    private lateinit var btnBack: ImageView
    private lateinit var eTextFirstName: TextInputEditText
    private lateinit var eTextLastName: TextInputEditText
    private lateinit var eTextEmail: TextInputEditText
    private lateinit var eTextContactNumber: TextInputEditText
    private lateinit var btnSaveChanges: MaterialButton
    private lateinit var presenter: ProfilePresenter
    private lateinit var userRepository: UserRepository
    private lateinit var auth: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemePreferences.applyTheme(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)

        // Initialize repositories and presenter
        auth = AuthRepository()
        userRepository = UserRepository()
        presenter = ProfilePresenter(this, AuthRepository(), UserRepository())

        // Initialize views AFTER setContentView
        btnBack = findViewById(R.id.btn_Back)
        btnSaveChanges = findViewById(R.id.btnSaveChanges)
        eTextFirstName = findViewById(R.id.etFirstName)
        eTextLastName = findViewById(R.id.etLastName)
        eTextEmail = findViewById(R.id.etEmail)
        eTextContactNumber = findViewById(R.id.etContactNumber)

        // Load user data
        presenter.getUserDetails()

        setupListeners()
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        btnSaveChanges.setOnClickListener {
            val user = auth.currentUser()
            if (user == null) {
                showMessage("No user logged in.")
                return@setOnClickListener
            }

            val firstName = eTextFirstName.text.toString().trim()
            val lastName = eTextLastName.text.toString().trim()
            val cNumber = eTextContactNumber.text.toString().trim()

            // Validation
            if (firstName.isEmpty()) {
                Toast.makeText(this, "First name is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (lastName.isEmpty()) {
                Toast.makeText(this, "Last name is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (cNumber.isEmpty()) {
                Toast.makeText(this, "Contact number is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (cNumber.length != 11) {
                Toast.makeText(this, "Invalid contact number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val name = "$firstName $lastName"
            val contactNumber = eTextContactNumber.text.toString().trim()

            setLoadingState(true)

            userRepository.updateUser(user.uid, name, contactNumber) { success, error ->
                // Hide loading state
                setLoadingState(false)

                if (success) {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, ProfileActivity::class.java))
                    finish()
                } else {
                    showMessage(error ?: "Failed to update user info in database.")
                }
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        btnSaveChanges.isEnabled = !isLoading
        btnSaveChanges.text = if (isLoading) "Saving..." else "Save Changes"
    }

    override fun showLoading() {
        // Implement if you have a progress bar
        // progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        // Implement if you have a progress bar
        // progressBar.visibility = View.GONE
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun navigateBack() {
        onBackPressedDispatcher.onBackPressed()
    }

    override fun showProfilePicture() {
        Log.d("EditProfileActivity", "Profile picture not yet implemented")
    }

    override fun showUserDetails(name: String, email: String, contactNumber: String) {
        val nameParts = name.trim().split(" ")
        val fAndM = (nameParts.size - 2).coerceAtLeast(0)
        val firstName = if (nameParts.isNotEmpty()) nameParts.slice(0..fAndM).joinToString(" ") else ""
        val lastName = if (nameParts.size > 1) nameParts[nameParts.size - 1] else ""

        eTextEmail.setText(email)
        eTextFirstName.setText(firstName)
        eTextLastName.setText(lastName)
        eTextContactNumber.setText(contactNumber)
    }
}