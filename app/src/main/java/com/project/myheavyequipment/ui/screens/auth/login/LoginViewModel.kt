package com.project.myheavyequipment.ui.screens.auth.login

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.project.myheavyequipment.data.repository.AuthRepository
import com.project.myheavyequipment.utils.AccountPreferences
import com.project.myheavyequipment.utils.Event
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val accountPreferences: AccountPreferences
) : ViewModel() {
    val isEmailError = MutableLiveData<Boolean>()
    val isPassError = MutableLiveData<Boolean>()

    val canLogin = MediatorLiveData<Boolean>().apply {
        var emailFlag = true
        var passFlag = true
        value = false
        addSource(isEmailError) { x ->
            x?.let {
                emailFlag = it
                value = !passFlag && !emailFlag
            }
        }
        addSource(isPassError) { x ->
            x?.let {
                passFlag = it
                value = !passFlag && !emailFlag
            }
        }
    }

    val isLoading = MutableLiveData<Boolean>()
    val toastMessage = MutableLiveData<Event<String>>()

    fun login(email: String, password: String) =
        authRepository.login(email, password)

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