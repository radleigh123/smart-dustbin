package com.eldroid.trashbincloud.view.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.contract.auth.AuthContract
import com.eldroid.trashbincloud.databinding.FragmentAuthRegisterBinding
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.presenter.auth.AuthPresenter

class RegisterFragment : Fragment(), AuthContract.View {

    private var _binding: FragmentAuthRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: AuthContract.Presenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAuthRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = AuthPresenter(this, AuthRepository())

        setupListeners()
    }

    private fun setupListeners() {
        binding.loginLinkTv.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.registerBtn.setOnClickListener {
            val firstName = binding.fNameEt.editText?.text.toString().trim()
            val lastName = binding.lNameEt.editText?.text.toString().trim()
            val email = binding.emailEt.editText?.text.toString().trim()
            val password = binding.passEt.editText?.text.toString()
            val confirmPassword = binding.pass2Et.editText?.text.toString().trim()
            val isChecked = binding.checkboxMeat.isChecked

            if (firstName.isEmpty()) {
                binding.fNameEt.error = "First name is required"
                binding.fNameEt.requestFocus()
                return@setOnClickListener
            }

            if (lastName.isEmpty()) {
                binding.lNameEt.error = "Last name is required"
                binding.lNameEt.requestFocus()
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                binding.emailEt.error = "Email is required"
                binding.emailEt.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.passEt.error = "Password is required"
                binding.passEt.requestFocus()
                return@setOnClickListener
            }

            if (password.length < 6) {
                binding.passEt.error = "Password must be at least 6 characters"
                binding.passEt.requestFocus()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                binding.pass2Et.error = "Passwords do not match"
                binding.pass2Et.requestFocus()
                return@setOnClickListener
            }

            if (!isChecked) {
                binding.checkboxMeat.error = "You must agree to the terms"
                binding.checkboxMeat.requestFocus()
                return@setOnClickListener
            }

            presenter.register(email, password)
        }
    }

    override fun showLoading() {
        binding.progressBar.visibility = ProgressBar.VISIBLE
    }

    override fun hideLoading() {
        binding.progressBar.visibility = ProgressBar.GONE
    }

    override fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun navigate() {
        Toast.makeText(requireContext(), "Registration success!", Toast.LENGTH_LONG).show()
        val intent = Intent(requireContext(), AuthActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}