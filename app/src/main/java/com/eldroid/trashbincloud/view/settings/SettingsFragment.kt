package com.eldroid.trashbincloud.view.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.eldroid.trashbincloud.databinding.FragmentSettingsBinding
import com.eldroid.trashbincloud.contract.user.UserContract
import com.eldroid.trashbincloud.model.repository.AuthRepository
import com.eldroid.trashbincloud.model.repository.UserRepository
import com.eldroid.trashbincloud.presenter.user.UserPresenter
import com.eldroid.trashbincloud.view.ProfileActivity
import com.eldroid.trashbincloud.view.auth.AuthActivity

class SettingsFragment : Fragment(), UserContract.View {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: UserContract.Presenter
    private lateinit var userRepository: UserRepository
    private lateinit var auth: AuthRepository

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)



        return binding.root
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = AuthRepository()
        userRepository = UserRepository()
        presenter = UserPresenter(auth, userRepository, this)
        presenter.getUserInfo()
        setupListeners()
    }

    private fun setupListeners() {
        binding.itemLogOut.setOnClickListener {
            auth.logout()
            navigateToLogin()
        }

        binding.constraintProfile.setOnClickListener {
            startActivity(
                Intent(requireContext(), ProfileActivity::class.java)
            )
        }
    }

    override fun loadUserInfo(name: String, email: String) {
        binding.topCardDetailsName.text = name
        binding.topCardDetailsEmail.text = email
    }

    override fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun navigateToLogin() {
        val intent = Intent(requireContext(), AuthActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
