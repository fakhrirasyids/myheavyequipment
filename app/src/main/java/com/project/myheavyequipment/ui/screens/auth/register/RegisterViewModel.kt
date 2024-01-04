package com.project.myheavyequipment.ui.screens.auth.register

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.project.myheavyequipment.data.repository.AuthRepository
import com.project.myheavyequipment.utils.AccountPreferences
import com.project.myheavyequipment.utils.Event
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository,
    private val accountPreferences: AccountPreferences
) : ViewModel() {
    val isNameError = MutableLiveData<Boolean>()
    val isEmailError = MutableLiveData<Boolean>()
    val isPassError = MutableLiveData<Boolean>()

    val canRegister = MediatorLiveData<Boolean>().apply {
        var nameFlag = true
        var emailFlag = true
        var passFlag = true
        value = false
        addSource(isNameError) { x ->
            x?.let {
                nameFlag = it
                value = !passFlag && !emailFlag && !nameFlag
            }
        }
        addSource(isEmailError) { x ->
            x?.let {
                emailFlag = it
                value = !passFlag && !emailFlag && !nameFlag
            }
        }
        addSource(isPassError) { x ->
            x?.let {
                passFlag = it
                value = !passFlag && !emailFlag && !nameFlag
            }
        }
    }

    val isLoading = MutableLiveData<Boolean>()
    val toastMessage = MutableLiveData<Event<String>>()

    fun register(name: String, email: String, password: String) =
        authRepository.register(name, email, password)

    fun saveToken(
        userID: String,
        userName: String,
        userEmail: String,
        userToken: String
    ) {
        viewModelScope.launch {
            accountPreferences.saveAccountPreferences(userID, userName, userEmail, userToken)
        }
    }

    fun getAccountToken() = accountPreferences.getAccountToken().asLiveData()
    fun getAccountID() = accountPreferences.getAccountID().asLiveData()
}