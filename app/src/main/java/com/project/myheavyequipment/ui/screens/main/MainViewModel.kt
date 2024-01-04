package com.project.myheavyequipment.ui.screens.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.project.myheavyequipment.data.model.PayloadItem
import com.project.myheavyequipment.data.repository.AlberRepository
import com.project.myheavyequipment.data.repository.AuthRepository
import com.project.myheavyequipment.utils.AccountPreferences
import com.project.myheavyequipment.utils.Event
import com.project.myheavyequipment.utils.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainViewModel(
    private val authRepository: AuthRepository,
    private val alberRepository: AlberRepository,
    private val accountPreferences: AccountPreferences
) : ViewModel() {

    val alberList = MutableLiveData<ArrayList<PayloadItem?>>(null)
    val errorState = MutableLiveData(0)

    private val job = Job()
    private var fetchScope = CoroutineScope(Dispatchers.Main + job)

    init {
        getAlberList()
    }

    fun getAlberList() {
        fetchScope.launch {
            errorState.postValue(0)
            alberRepository.invokeAlberList().asFlow().collect { result ->
                when (result) {
                    is Result.Loading -> {
                    }

                    is Result.Success -> {
                        if (result.data.payload.isNullOrEmpty()) {
                            alberList.postValue(ArrayList())
                        } else {
                            alberList.postValue((result.data.payload as ArrayList<PayloadItem?>?)!!)
                        }
                    }

                    is Result.Error -> {
                        errorState.postValue(0)
                    }

                    is Result.ErrorFirstFetch -> {
                        errorState.postValue(1)
                    }
                }
            }
        }
    }

    fun pauseJob() {
        job.cancelChildren()
    }

    fun scanQRCode(uniqueCode: String) = alberRepository.scanQRCode(uniqueCode)

    fun deleteAlber(index: Int) = alberRepository.deleteAlber(index)

    fun clearAccountPreferences() {
        viewModelScope.launch {
            accountPreferences.clearAccountPreferences()
        }
    }

    fun getAccountName() = accountPreferences.getAccountName().asLiveData()

    fun logoutAccount() = authRepository.logout()
}