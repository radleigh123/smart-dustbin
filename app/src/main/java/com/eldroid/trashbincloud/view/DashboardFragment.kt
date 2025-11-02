package com.eldroid.trashbincloud.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.eldroid.trashbincloud.contract.DashboardContract
import com.eldroid.trashbincloud.databinding.FragmentMainDashboardBinding
import com.eldroid.trashbincloud.model.entity.TrashBin
import com.eldroid.trashbincloud.model.entity.User
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.model.repository.TrashBinRepository
import com.eldroid.trashbincloud.model.repository.UserRepository
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

        presenter = DashboardPresenter(this, AuthRepository(), UserRepository(), TrashBinRepository())
        presenter.getUserInfo()
        presenter.attachView(this)

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

        binding.notificationIcon.setOnClickListener {
            val intent = Intent(requireContext(), Notification::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
        _binding = null
    }

    override fun showSkeleton() {
    }

    override fun hideSkeleton() {
    }

    override fun showMessage(message: String) {
        Log.d("DashboardFragment", message)
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun loadUserInfo(user: User) {
        val name: String? = user.name?.split(" ")[0]
        binding.greeting.text = "Greetings, ${name}"
        binding.welcomeText.text = "Welcome, ${name}"
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