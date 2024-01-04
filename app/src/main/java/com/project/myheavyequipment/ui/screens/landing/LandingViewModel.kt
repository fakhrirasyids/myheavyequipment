package com.project.myheavyequipment.ui.screens.landing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.project.myheavyequipment.utils.AccountPreferences

class LandingViewModel(private val accountPreferences: AccountPreferences) : ViewModel() {
    fun getAccountToken() = accountPreferences.getAccountToken().asLiveData()
    fun getAccountID() = accountPreferences.getAccountID().asLiveData()
}