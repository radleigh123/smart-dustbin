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
import com.eldroid.trashbincloud.model.repository.UserRepository
import com.eldroid.trashbincloud.presenter.settings.SettingsPresenter
import com.eldroid.trashbincloud.view.profile.ProfileActivity
import com.eldroid.trashbincloud.view.auth.AuthActivity

class SettingsFragment : Fragment(), SettingsContract.View {
    private var _binding: FragmentSettingsBinding? = null
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

        presenter = SettingsPresenter(this, UserRepository(), AuthRepository())
        presenter.getUserInfo()

        setupListeners()
    }

    private fun setupListeners() {
        binding.itemLogOut.setOnClickListener {
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

    override fun showMessage(message: String) {
        Log.d("SettingsFragment", message)
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun loadUserInfo(name: String, email: String, contactNumber: String) {
        binding.topCardDetailsName.text = name
        binding.topCardDetailsEmail.text = email
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}