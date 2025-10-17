package com.eldroid.trashbincloud.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.eldroid.trashbincloud.contract.DashboardContract
import com.eldroid.trashbincloud.databinding.FragmentMainDashboardBinding
import com.eldroid.trashbincloud.model.entity.TrashBin
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.model.repository.TrashBinRepository
import com.eldroid.trashbincloud.presenter.DashboardPresenter
import com.eldroid.trashbincloud.view.bin.AddBinActivity

class DashboardFragment : Fragment(), DashboardContract.View {

    private var _binding: FragmentMainDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: DashboardContract.Presenter
    private lateinit var adapter: TrashBinAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TrashBinAdapter(emptyList())
        binding.recyclerViewBins.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewBins.adapter = adapter

        presenter = DashboardPresenter(this, AuthRepository(), TrashBinRepository())
        presenter.getUserInfo()

        setupClickListeners()
        /*binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_DashboardFragment_to_SecondFragment)
        }*/
    }

    private fun setupClickListeners() {
        binding.addBinBtn.setOnClickListener {
            // startActivity(Intent(requireContext(), AddBinActivity::class.java))
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
    }

    override fun hideSkeleton() {
    }

    override fun showError(message: String) {
    }

    override fun loadUserInfo(name: String, email: String) {
        binding.greeting.text = "Greetings, $name"
        binding.welcomeText.text = "Welcome, $email"
    }

    override fun showBins(bins: List<TrashBin>) {
        adapter.updateBins(bins)
        binding.sectionDefault.visibility = View.GONE
        binding.sectionSuccess.visibility = View.VISIBLE
        binding.binCount.text = "${bins.size} bins active today"
    }

    override fun showNoBins() {
        binding.sectionDefault.visibility = View.VISIBLE
        binding.sectionSuccess.visibility = View.GONE
        binding.binCount.text = "No bins connected"
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}