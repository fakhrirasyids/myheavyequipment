package com.project.myheavyequipment.ui.screens.landing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.project.myheavyequipment.data.factory.AuthViewModelFactory
import com.project.myheavyequipment.data.remote.ApiConfig
import com.project.myheavyequipment.databinding.ActivityLandingBinding
import com.project.myheavyequipment.ui.screens.auth.login.LoginActivity
import com.project.myheavyequipment.ui.screens.auth.login.LoginViewModel
import com.project.myheavyequipment.ui.screens.auth.register.RegisterActivity
import com.project.myheavyequipment.ui.screens.main.MainActivity
import com.project.myheavyequipment.ui.screens.main.MainActivity.Companion.ACCOUNT_ID
import com.project.myheavyequipment.ui.screens.main.MainActivity.Companion.ACCOUNT_TOKEN
import com.project.myheavyequipment.utils.AccountPreferences
import com.project.myheavyequipment.utils.dataStore

class LandingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLandingBinding
    private val landingViewModel by viewModels<LandingViewModel> {
        AuthViewModelFactory(
            apiService = null,
            accountPreferences = AccountPreferences.getPrefInstance(dataStore)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeLogin()
        setListeners()
    }

    private fun observeLogin() {
        landingViewModel.getAccountToken().observe(this) { preferences ->
            if (preferences != AccountPreferences.preferenceDefaultValue) {
                landingViewModel.getAccountID().observe(this) { userID ->
                    Log.i("LENDENG", "getAlberHistoryList: ${preferences}")

                    val iMain = Intent(this, MainActivity::class.java)
                    iMain.putExtra(ACCOUNT_TOKEN, preferences)
                    iMain.putExtra(ACCOUNT_ID, userID)
                    startActivity(iMain)
                    finishAffinity()
                }
            }
        }
    }

    private fun setListeners() {
        binding.apply {
            btnLogin.setOnClickListener {
                val iLogin = Intent(this@LandingActivity, LoginActivity::class.java)
                startActivity(iLogin)
            }

            btnRegister.setOnClickListener {
                val iRegister = Intent(this@LandingActivity, RegisterActivity::class.java)
                startActivity(iRegister)
            }
        }
    }
}