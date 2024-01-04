package com.project.myheavyequipment.ui.screens.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.project.myheavyequipment.R
import com.project.myheavyequipment.data.factory.AuthViewModelFactory
import com.project.myheavyequipment.data.factory.MainViewModelFactory
import com.project.myheavyequipment.databinding.ActivityProfileBinding
import com.project.myheavyequipment.ui.screens.landing.LandingViewModel
import com.project.myheavyequipment.ui.screens.main.MainActivity
import com.project.myheavyequipment.ui.screens.main.MainViewModel
import com.project.myheavyequipment.utils.AccountPreferences
import com.project.myheavyequipment.utils.dataStore

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    private val profileViewModel by viewModels<ProfileViewModel> {
        MainViewModelFactory(
            accToken = null,
            apiService = null,
            accountPreferences = AccountPreferences.getPrefInstance(dataStore)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeAccountInfo()
        setToolbar()
    }

    private fun observeAccountInfo() {
        profileViewModel.getAccountName().observe(this) { name ->
            profileViewModel.getAccountEmail().observe(this) { email ->
                if (name != AccountPreferences.preferenceDefaultValue && email != AccountPreferences.preferenceDefaultValue) {
                    binding.apply {
                        tvAccountName.text = name
                        tvAccountEmail.text = email
                    }
                }
            }
        }
    }

    private fun setToolbar() {
        binding.apply {
            toolbar.navigationIcon?.mutate()?.let {
                it.setTint(ContextCompat.getColor(this@ProfileActivity, R.color.orange))
                binding.toolbar.navigationIcon = it
            }

            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent()
        setResult(RESULT_OK, intent)
        finish()
    }
}