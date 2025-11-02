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
import com.eldroid.trashbincloud.view.MainActivity
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.contract.auth.AuthContract
import com.eldroid.trashbincloud.databinding.FragmentAuthLoginBinding
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.model.repository.UserRepository
import com.eldroid.trashbincloud.presenter.auth.AuthPresenter

class LoginFragment : Fragment(), AuthContract.View {

    private var _binding: FragmentAuthLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: AuthContract.Presenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAuthLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = AuthPresenter(this, AuthRepository(), UserRepository())

        setupListeners()
    }

    private fun setupListeners() {
        binding.registerLinkTv.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.forgotPasswordLinkTv.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }

        binding.loginBtn.setOnClickListener {
            val email = binding.emailEt.editText?.text.toString().trim()
            val password = binding.passEt.editText?.text.toString().trim()
            presenter.login(email, password)
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
        Toast.makeText(requireContext(), "Login success!", Toast.LENGTH_LONG).show()
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}