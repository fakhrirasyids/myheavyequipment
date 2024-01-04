package com.project.myheavyequipment.ui.screens.addalber

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.myheavyequipment.data.repository.AlberRepository
import com.project.myheavyequipment.utils.AccountPreferences
import com.project.myheavyequipment.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddAlberViewModel(
    private val alberRepository: AlberRepository
) : ViewModel() {
    val showMoreInput = MutableLiveData(false)
    val toastMessage = MutableLiveData<Event<String>>()

    suspend fun postAlberToQRCode(
        jenis: String,
        type: String,
        hours_meter: Int,
        capacity: Int,
        engine: String,
        lifting_height: Int?,
        stage: Int?,
        load_center: Int?,
    ) =
        alberRepository.postAlberToQRCode(
            jenis,
            type,
            hours_meter,
            capacity,
            engine,
            lifting_height,
            stage,
            load_center
        )

}