package com.eldroid.trashbincloud.view.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.eldroid.trashbincloud.contract.settings.SettingsContract
import com.eldroid.trashbincloud.databinding.FragmentSettingsBinding
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.model.repository.UserRepository
import com.eldroid.trashbincloud.presenter.settings.SettingsPresenter
import com.eldroid.trashbincloud.view.ChangePassword
import com.eldroid.trashbincloud.view.auth.AuthActivity
import com.eldroid.trashbincloud.view.profile.EditProfileActivity
import com.eldroid.trashbincloud.view.userguide.UserGuideActivity
import com.eldroid.trashbincloud.R

class SettingsFragment : Fragment(), SettingsContract.View {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var presenter: SettingsContract.Presenter
    private var isUserInteraction = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = SettingsPresenter(
            this,
            UserRepository(),
            AuthRepository(),
            requireContext()
        )

        presenter.getUserInfo()
        presenter.loadThemePreference()

        setupListeners()
    }

    private fun setupListeners() {
        binding.menuEditProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        binding.menuChangePassword.setOnClickListener {
            startActivity(Intent(requireContext(), ChangePassword::class.java))
        }

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isUserInteraction) {
                presenter.toggleTheme(isChecked)
            }
        }

        binding.itemLogOut.setOnClickListener {
            showLogoutConfirmation()
        }

        binding.backArrow.setOnClickListener {
            startActivity(
                Intent(requireContext(), MainActivity::class.java)
            )
        }

        binding.constraintProfile.setOnClickListener {
            startActivity(
                Intent(requireContext(), ProfileActivity::class.java)
            )
        }
        binding.llUserGuide.setOnClickListener {
            startActivity(Intent(requireContext(), UserGuideActivity::class.java))
        }

        binding.menuWifiConnection.setOnClickListener {
            showMessage("WiFi Connection - Coming soon")
        }

        binding.menuAddNewDevice.setOnClickListener {
            showMessage("Add New Device - Coming soon")
        }

        binding.menuLinkedBins.setOnClickListener {
            showMessage("Linked Bins - Coming soon")
        }

        binding.menuLanguage.setOnClickListener {
            showMessage("Language selection - Coming soon")
        }

        binding.menuNotifications.setOnClickListener {
            showMessage("Notifications - Coming soon")
        }

        binding.menuFaq.setOnClickListener {
            showMessage("FAQ - Coming soon")
        }

        binding.menuUserGuide.setOnClickListener {
            startActivity(Intent(requireContext(), UserGuideActivity::class.java))
        }


        binding.menuContactSupport.setOnClickListener {
            showMessage("Contact Support - Coming soon")
        }

        binding.menuPrivacyPolicy.setOnClickListener {
            showMessage("Privacy Policy - Coming soon")
        }

        binding.menuTermsConditions.setOnClickListener {
            findNavController().navigate(R.id.action_Settings_to_SecondFragment)
        }
    }

    private fun showLogoutConfirmation() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                presenter.logout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun navigateToLogin() {
        val intent = Intent(requireContext(), AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun showMessage(message: String) {
        Log.d("SettingsFragment", message)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun loadUserInfo(name: String, email: String, contactNumber: String) {
        binding.topCardDetailsName.text = name
        binding.topCardDetailsEmail.text = email
    }

    override fun updateThemeSwitch(isDarkMode: Boolean) {
        _binding?.let {
            isUserInteraction = false
            it.switchDarkMode.isChecked = isDarkMode
            isUserInteraction = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}