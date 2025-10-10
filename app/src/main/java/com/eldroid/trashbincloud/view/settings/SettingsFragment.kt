package com.eldroid.trashbincloud.view.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.eldroid.trashbincloud.contract.settings.SettingsContract
import com.eldroid.trashbincloud.databinding.FragmentSettingsBinding
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.presenter.settings.SettingsPresenter
import com.eldroid.trashbincloud.view.ProfileActivity
import com.eldroid.trashbincloud.view.auth.AuthActivity
import com.google.android.material.snackbar.Snackbar

class SettingsFragment : Fragment(), SettingsContract.View {
    private var _binding: FragmentSettingsBinding?=null
    private val binding get() = _binding!!

    private lateinit var presenter: SettingsContract.Presenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = SettingsPresenter(this, AuthRepository())

        presenter.getUserInfo()
        setupListeners()
    }

    private fun setupListeners() {
        binding.itemLogOut.setOnClickListener {
            // Toast.makeText(context, "Log out Clicked", Toast.LENGTH_SHORT).show()
            presenter.logout()
        }
        binding.constraintProfile.setOnClickListener {
            startActivity(
                Intent(requireContext(), ProfileActivity::class.java)
            )
        }
    }

    override fun navigateToLogin() {
        val intent = Intent(requireContext(), AuthActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun showError(message: String) {
        TODO("Not yet implemented")
    }

    override fun loadUserInfo(name: String, email: String) {
        binding.topCardDetailsName.text = if (name.isEmpty()) name else "UNKNOWN"
        binding.topCardDetailsEmail.text = email
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}