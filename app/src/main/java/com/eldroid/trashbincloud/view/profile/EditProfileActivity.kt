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


    private lateinit var menuChangePassword: LinearLayout
    private lateinit var auth: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)
        auth = AuthRepository()
        userRepository = UserRepository()
        presenter = ProfilePresenter(this, AuthRepository(), UserRepository())
        presenter.getUserDetails()
        btnBack = findViewById(R.id.btnBack)
        btnSaveChanges = findViewById(R.id.btnSaveChanges)
        eTextFirstName = findViewById(R.id.etFirstName)
        eTextLastName = findViewById(R.id.etLastName)
        menuChangePassword = findViewById(R.id.menuChangePassword)
        eTextEmail = findViewById(R.id.etEmail)
        eTextContactNumber = findViewById(R.id.etContactNumber)

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
            val cNumber = eTextContactNumber.text.toString().trim()
            if (user == null) {
                showMessage("No user logged in.")
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

            if(cNumber.isEmpty()){
                Toast.makeText(this, "Contact number is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(cNumber.length != 11){
                Toast.makeText(this, "Invalid contact number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val name = eTextFirstName.text.toString().trim() + " " + eTextLastName.text.toString().trim()
            val contactNumber = eTextContactNumber.text.toString().trim()


            userRepository.updateUser(user.uid, name, contactNumber) { success, error ->
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

    override fun showLoading() {
         ProgressBar.VISIBLE
    }

    override fun hideLoading() {
         ProgressBar.GONE
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun navigateBack() {
        TODO("Not yet implemented")
    }

    override fun showProfilePicture() {
        Log.d("ProfileActivity", "Profile picture not yet implemented")
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