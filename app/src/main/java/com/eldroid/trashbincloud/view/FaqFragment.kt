package com.eldroid.trashbincloud.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.contract.FaqContract
import com.eldroid.trashbincloud.presenter.FaqPresenter
import com.google.android.material.button.MaterialButton

class FaqFragment : Fragment(), FaqContract.View {
    private lateinit var presenter: FaqPresenter
    private lateinit var backBtn: ImageButton
    private lateinit var contactSupportBtn: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_faq, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backBtn = view.findViewById(R.id.back_btn)
        contactSupportBtn = view.findViewById(R.id.contact_support_btn)

        presenter = FaqPresenter(this)

        setupListeners()
    }

    private fun setupListeners() {
        backBtn.setOnClickListener {
            presenter.onBackPressed()
        }

        contactSupportBtn.setOnClickListener {
            presenter.onContactSupportClicked()
        }
    }

    override fun navigateToContactSupport() {
        val intent = Intent(requireContext(), ContactSupportActivity::class.java)
        startActivity(intent)
    }

    override fun navigateBack() {
        findNavController().navigateUp()
    }
}