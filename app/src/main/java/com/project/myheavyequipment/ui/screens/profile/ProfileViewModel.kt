package com.project.myheavyequipment.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.project.myheavyequipment.utils.AccountPreferences

class ProfileViewModel(private val accountPreferences: AccountPreferences) : ViewModel() {
    fun getAccountName() = accountPreferences.getAccountName().asLiveData()
    fun getAccountEmail() = accountPreferences.getAccountEmail().asLiveData()
}