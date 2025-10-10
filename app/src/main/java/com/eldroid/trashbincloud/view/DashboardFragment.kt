package com.eldroid.trashbincloud.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eldroid.trashbincloud.contract.DashboardContract
import com.eldroid.trashbincloud.databinding.FragmentMainDashboardBinding
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.presenter.DashboardPresenter
import com.eldroid.trashbincloud.view.bin.AddBinActivity

class DashboardFragment : Fragment(), DashboardContract.View {

    private var _binding: FragmentMainDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: DashboardContract.Presenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = DashboardPresenter(this, AuthRepository())

        presenter.getUserInfo()
        setupClickListeners()
        /*binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_DashboardFragment_to_SecondFragment)
        }*/
    }

    private fun setupClickListeners() {
        binding.addBinBtn.setOnClickListener {
            val intent = Intent(requireContext(), AddBinActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun showSkeleton() {
        TODO("Not yet implemented")
    }

    override fun hideSkeleton() {
        TODO("Not yet implemented")
    }

    override fun showError(message: String) {
        TODO("Not yet implemented")
    }

    override fun loadUserInfo(name: String, email: String) {
        binding.greeting.text = "Greetings, $email"
        binding.welcomeText.text = "Welcome, $email"
    }

}