package com.project.myheavyequipment.data.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.myheavyequipment.data.remote.ApiService
import com.project.myheavyequipment.data.repository.AlberRepository
import com.project.myheavyequipment.data.repository.AuthRepository
import com.project.myheavyequipment.ui.screens.addalber.AddAlberViewModel
import com.project.myheavyequipment.ui.screens.auth.login.LoginViewModel
import com.project.myheavyequipment.ui.screens.auth.register.RegisterViewModel
import com.project.myheavyequipment.ui.screens.landing.LandingViewModel
import com.project.myheavyequipment.ui.screens.main.MainViewModel
import com.project.myheavyequipment.ui.screens.profile.ProfileViewModel
import com.project.myheavyequipment.utils.AccountPreferences

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(
    private val accToken: String?,
    private val apiService: ApiService?,
    private val accountPreferences: AccountPreferences
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(
                    AuthRepository(accToken, apiService!!),
                    AlberRepository(accToken!!, apiService),
                    accountPreferences
                ) as T
            }

            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(accountPreferences) as T
            }

            modelClass.isAssignableFrom(AddAlberViewModel::class.java) -> {
                AddAlberViewModel(
                    AlberRepository(accToken!!, apiService!!)
                ) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}