package com.eldroid.trashbincloud.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.contract.TermsConditionsContract
import com.eldroid.trashbincloud.databinding.FragmentTermsAndConditionsBinding
import com.eldroid.trashbincloud.presenter.TermsConditionsPresenter

class TermsConditionsFragment : Fragment(), TermsConditionsContract.View {

    private var _binding: FragmentTermsAndConditionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: TermsConditionsPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTermsAndConditionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = TermsConditionsPresenter(this)

        setupListeners()
    }

    private fun setupListeners() {
        binding.backBtn.setOnClickListener {
            presenter.onBackPressed()
        }

        binding.agreeBtn.setOnClickListener {
            presenter.onAgreeClicked()
        }

        binding.declineBtn.setOnClickListener {
            presenter.onDeclineClicked()
        }
    }

    override fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun navigateToDashboard() {
        findNavController().navigate(
            R.id.action_termsConditionsFragment_to_dashboardFragment
        )
    }

    override fun navigateBack() {
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}