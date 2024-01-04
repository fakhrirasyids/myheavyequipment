package com.project.myheavyequipment.data.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.myheavyequipment.data.remote.ApiService
import com.project.myheavyequipment.data.repository.AuthRepository
import com.project.myheavyequipment.ui.screens.auth.login.LoginViewModel
import com.project.myheavyequipment.ui.screens.auth.register.RegisterViewModel
import com.project.myheavyequipment.ui.screens.landing.LandingViewModel
import com.project.myheavyequipment.utils.AccountPreferences

@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory(
    private val apiService: ApiService?,
    private val accountPreferences: AccountPreferences
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(AuthRepository(null, apiService!!), accountPreferences) as T
            }

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(AuthRepository(null, apiService!!), accountPreferences) as T
            }

            modelClass.isAssignableFrom(LandingViewModel::class.java) -> {
                LandingViewModel(accountPreferences) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}