package com.eldroid.trashbincloud.view.bin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.eldroid.trashbincloud.R
import com.eldroid.trashbincloud.databinding.ActivityAddBinBinding

class AddBinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddBinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_content_main_bin)
    }

}