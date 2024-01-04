package com.project.myheavyequipment.data.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.myheavyequipment.data.remote.ApiService
import com.project.myheavyequipment.data.repository.AlberRepository
import com.project.myheavyequipment.ui.screens.history.HistoryViewModel

@Suppress("UNCHECKED_CAST")
class HistoryViewModelFactory(
    private val accToken: String,
    private val alberId: Int,
    private val apiService: ApiService,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return when {
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(
                    AlberRepository(accToken, apiService),
                    alberId
                ) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}