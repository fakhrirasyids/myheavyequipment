package com.project.myheavyequipment.ui.screens.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import com.project.myheavyequipment.data.model.HistoryItem
import com.project.myheavyequipment.data.repository.AlberRepository
import com.project.myheavyequipment.utils.Event
import com.project.myheavyequipment.utils.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val alberRepository: AlberRepository,
    private val alberId: Int,
) : ViewModel() {
    val latestStatus = MutableLiveData(1)
    val errorState = MutableLiveData(0)
    val alberHistory = MutableLiveData<ArrayList<HistoryItem?>>(null)
    val errorMessage = MutableLiveData<Event<String>>()

    private val _firstHistoryItem = MutableLiveData<HistoryItem?>()
    val firstHistoryItem: LiveData<HistoryItem?> = _firstHistoryItem

    private val job = Job()
    private var fetchHistoryScope = CoroutineScope(Dispatchers.Main + job)

    init {
        getAlberHistoryList()
    }

    fun getAlberHistoryList() {
        fetchHistoryScope.launch {
            errorState.postValue(0)
            alberRepository.invokeAlberReparationHistory(alberId).asFlow().collect { result ->
                when (result) {
                    is Result.Loading -> {
                    }

                    is Result.Success -> {
                        val alberHistoryList = (result.data.payload as ArrayList<HistoryItem?>?)!!

                        if (alberHistoryList.isEmpty()) {
                            alberHistory.postValue(ArrayList())
                        } else {
                            latestStatus.postValue(Integer.parseInt(alberHistoryList[0]?.status.toString()))
                            if (alberHistoryList[0]?.status == "2") {
                                _firstHistoryItem.postValue(alberHistoryList[0])
                                alberHistoryList.removeAt(0)
                            }
                            alberHistory.postValue(alberHistoryList)
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

    fun updateAlberReparationToWork(index: Int, reparationIndex: Int) =
        alberRepository.updateAlberReparationToWork(index, reparationIndex)

    fun storeAlberReparation(index: Int, hoursMeter: Int, note: String?) =
        alberRepository.storeAlberReparation(index, hoursMeter, note)
}