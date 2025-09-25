package com.eldroid.trashbincloud.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.databinding.FragmentMainDashboardBinding
import com.eldroid.trashbincloud.model.entity.TrashBin
import com.eldroid.trashbincloud.model.repository.TrashBinRepository

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class DashboardFragment : Fragment() {

    private var _binding: FragmentMainDashboardBinding? = null
    private val binding get() = _binding!!

    private val trashBinAdapter = TrashBinAdapter { bin ->
        onTrashBinClicked(bin)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMainDashboardBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        loadTrashBins()

//        binding.buttonFirst.setOnClickListener {
//            findNavController().navigate(R.id.action_DashboardFragment_to_SecondFragment)
//        }
    }


    private fun loadTrashBins() {
        // Here you would get your data from a repository or viewModel
        val trashBinRepository = TrashBinRepository()
        /*trashBinRepository.getAllBins { trashBins, errorMessage ->
            if (errorMessage == null) {
                trashBinAdapter.submitList(trashBins)
            } else {
                Toast.makeText(requireContext(), "Error: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        }*/

        // For testing, you can use sample data:
        val sampleBins = listOf(
            TrashBin(
                binId = "bin1",
                name = "Main Building Bin",
                location = "Floor 1, Main Building",
                fillLevel = 75,
                status = "warning",
                lastUpdated = System.currentTimeMillis(),
                battery = 80,
                temperature = 22.5f
            ),
            TrashBin(
                binId = "bin2",
                name = "Cafeteria Bin",
                location = "Cafeteria",
                fillLevel = 30,
                status = "normal",
                lastUpdated = System.currentTimeMillis() - 3600000, // 1 hour ago
                battery = 95,
                temperature = 21.0f
            )
        )
        trashBinAdapter.submitList(sampleBins)
    }

    private fun onTrashBinClicked(bin: TrashBin) {
        // Handle bin click event
        Toast.makeText(requireContext(), "Selected bin: ${bin.name}", Toast.LENGTH_SHORT).show()

        // Navigate to detail screen if needed
        // val action = DashboardFragmentDirections.actionFirstFragmentToTrashBinDetailFragment(bin.binId)
        // findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}