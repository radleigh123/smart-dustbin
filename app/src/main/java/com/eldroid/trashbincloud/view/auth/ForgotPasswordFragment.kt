package com.eldroid.trashbincloud.view.auth

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
import com.eldroid.trashbincloud.databinding.FragmentAuthForgotPasswordBinding
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.model.repository.UserRepository
import com.eldroid.trashbincloud.presenter.auth.AuthPresenter

class ForgotPasswordFragment : Fragment(), AuthContract.View {

    private var _binding: FragmentAuthForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: AuthContract.Presenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAuthForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = AuthPresenter(this, AuthRepository(), UserRepository())

        setupListeners()
    }

    private fun setupListeners() {
        binding.sendLinkTv.setOnClickListener {
            findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
        }

        binding.sendEmailBtn.setOnClickListener {
            val email = binding.emailEt.editText?.text.toString().trim()
            presenter.sendResetPasswordEmail(email)
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
        findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}