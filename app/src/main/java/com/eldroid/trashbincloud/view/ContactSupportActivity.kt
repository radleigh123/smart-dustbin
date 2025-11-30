package com.eldroid.trashbincloud.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.contract.ContactSupportContract
import com.eldroid.trashbincloud.presenter.ContactSupportPresenter

class ContactSupportActivity : AppCompatActivity(), ContactSupportContract.View {

    private lateinit var presenter: ContactSupportPresenter

    // View references
    private lateinit var backBtn: ImageButton
    private lateinit var emailContactRow: LinearLayout
    private lateinit var phoneContactRow: LinearLayout
    private lateinit var emailAddressText: TextView
    private lateinit var phoneNumberText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_contact_support)

        initializeViews()

        presenter = ContactSupportPresenter()
        presenter.attachView(this)

        setupListeners()
    }

    private fun initializeViews() {
        backBtn = findViewById(R.id.back_btn)
        emailContactRow = findViewById(R.id.email_contact_row)
        phoneContactRow = findViewById(R.id.phone_contact_row)
        emailAddressText = findViewById(R.id.email_address_text)
        phoneNumberText = findViewById(R.id.phone_number_text)
    }

    private fun setupListeners() {
        backBtn.setOnClickListener {
            finish()
        }

        emailContactRow.setOnClickListener {
            val email = emailAddressText.text.toString()
            presenter.onEmailContactClicked(email)
        }

        phoneContactRow.setOnClickListener {
            val phone = phoneNumberText.text.toString()
            presenter.onPhoneContactClicked(phone)
        }
    }

    override fun openEmailClient(email: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
                putExtra(Intent.EXTRA_SUBJECT, "Support Request - Loadout")
            }

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(Intent.createChooser(intent, "Send Email"))
            } else {
                showError("No email client found")
            }
        } catch (e: Exception) {
            showError("Error opening email client: ${e.message}")
        }
    }

    override fun openPhoneDialer(phone: String) {
        try {
            val cleanPhone = phone.replace(Regex("[^0-9+]"), "")
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$cleanPhone")
            }

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                showError("Cannot open phone dialer")
            }
        } catch (e: Exception) {
            showError("Error opening dialer: ${e.message}")
        }
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}