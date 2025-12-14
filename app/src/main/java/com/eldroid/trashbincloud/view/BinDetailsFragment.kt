package com.eldroid.trashbincloud.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.contract.BinDetailsContract
import com.eldroid.trashbincloud.databinding.FragmentBinDetailsBinding
import com.eldroid.trashbincloud.model.entity.ActivityEvent
import com.eldroid.trashbincloud.model.entity.TrashBin
import com.eldroid.trashbincloud.model.repository.ActivityRepository
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.model.repository.TrashBinRepository
import com.eldroid.trashbincloud.presenter.BinDetailsPresenter
import com.eldroid.trashbincloud.view.bin.AddBinSetupFragmentArgs

class BinDetailsFragment : Fragment(), BinDetailsContract.View {

    private var _binding: FragmentBinDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var authRepository: AuthRepository

    private lateinit var actRepository: ActivityRepository

    private lateinit var activityRecyclerView: RecyclerView
    private lateinit var activityAdapter: SingleHistoryAdapter
    private lateinit var presenter: BinDetailsContract.Presenter
    private val args: BinDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBinDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authRepository = AuthRepository()
        actRepository = ActivityRepository()

        presenter = BinDetailsPresenter(requireContext(), authRepository, actRepository)
        presenter.attachView(this)

        activityRecyclerView = binding.rvRecentActivity

        setupMenuButton()
        setupBackButton()
        setupRecyclerView()
        loadBinFromArguments()
    }

    private fun loadBinFromArguments() {
        val bin = args.bin
        presenter.loadBinData(bin)

    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupMenuButton() {
        binding.btnMenu.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }

    private fun setupRecyclerView() {
        activityAdapter = SingleHistoryAdapter(emptyList())
        activityRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = activityAdapter
        }
    }

    private fun showPopupMenu(view: View) {
        val wrapper = android.view.ContextThemeWrapper(requireContext(), R.style.CustomPopupMenu)
        val popupMenu = PopupMenu(wrapper, view)
        popupMenu.menuInflater.inflate(R.menu.bin_details_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_edit_details -> {
                    Toast.makeText(
                        requireContext(),
                        "Edit Details clicked",
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
        _binding = null
    }

    override fun showBinDetails(bin: TrashBin) {
        binding.tvBinName.text = bin.name
        binding.tvLocation.text = bin.location
        binding.tvFillLevel.text = "${bin.fillLevel}%"
        binding.progressBarCircular.progress = bin.fillLevel?.toInt() ?: 0
        binding.tvFillPercentage.text = "${bin.fillLevel}%"

        when {
            bin.fillLevel!! >= 76 -> {
                binding.tvAlertMessage.visibility = View.VISIBLE
                binding.tvAlertMessage.text = "⚠️ Bin is nearly full"
            }
            else -> {
                binding.tvAlertMessage.visibility = View.GONE
            }
        }
    }

    override fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    override fun findViewById(
        statusDot: Int,
        tvActivityTitle: Int,
        tvActivityTime: Int,
        ivInfo: Int
    ): BinDetailsContract.View {
        TODO("Not yet implemented")
    }

    override fun showActivities(activities: List<ActivityEvent>) {
        activityAdapter.updateActivities(activities)
    }

    override fun showNoActivities(activities: List<ActivityEvent>) {
        TODO("Not yet implemented")
    }
}