package com.project.myheavyequipment.data.repository

import android.util.Log
import androidx.lifecycle.liveData
import com.project.myheavyequipment.data.remote.ApiService
import com.project.myheavyequipment.utils.Result
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive

class AlberRepository(
    private val accToken: String,
    private val apiService: ApiService
) {
    suspend fun postAlberToQRCode(
        jenis: String,
        type: String,
        hours_meter: Int,
        capacity: Int,
        engine: String,
        lifting_height: Int?,
        stage: Int?,
        load_center: Int?,
    ) = liveData {
        emit(Result.Loading)
        try {
            val token = "Bearer $accToken"
            val registerResponse = apiService.postAlberToQRCode(
                token,
                jenis,
                type,
                hours_meter,
                capacity,
                engine,
                lifting_height,
                stage,
                load_center
            )
            emit(Result.Success(registerResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }

    fun scanQRCode(
        uniqueCode: String,
    ) = liveData {
        emit(Result.Loading)
        try {
            val token = "Bearer $accToken"
            val scanResponse = apiService.scanQRCode(
                token, uniqueCode
            )
            emit(Result.Success(scanResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }

    fun invokeAlberList() = liveData {
        var isFirstLoop = true
        do {
            emit(Result.Loading)

            try {
                val token = "Bearer $accToken"
                val listAlber = apiService.getAlberList(token)
                emit(Result.Success(listAlber))
                kotlinx.coroutines.delay(2000)
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
                if (isFirstLoop) {
                    emit(Result.ErrorFirstFetch(e.message.toString()))
                    return@liveData
                }
            }

            isFirstLoop = false
        } while (currentCoroutineContext().isActive)
    }

    suspend fun invokeAlberReparationHistory(index: Int) = liveData {
        var isFirstLoop = true
        do {
            emit(Result.Loading)

            try {
                val token = "Bearer $accToken"
                val historyAlberResponse = apiService.getAlberReparationHistory(token, index)
                emit(Result.Success(historyAlberResponse))
                kotlinx.coroutines.delay(2000)
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
                Log.i("TAG", "invokeAlberDetail: ${e.message.toString()}")
                if (isFirstLoop) {
                    emit(Result.ErrorFirstFetch(e.message.toString()))
                    return@liveData
                }
            }

            isFirstLoop = false
        } while (currentCoroutineContext().isActive)
    }

    fun updateAlberReparationToWork(index: Int, reparationIndex: Int) = liveData {
        emit(Result.Loading)
        try {
            val token = "Bearer $accToken"
            val updateAlberReparation =
                apiService.updateAlberReparationToWork(token, index, reparationIndex)
            emit(Result.Success(updateAlberReparation))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
            Log.i("TAG", "invokeAlberDetail: ${e.message}")
        }
    }

    fun storeAlberReparation(index: Int, hoursMeter: Int, note: String?) = liveData {
        emit(Result.Loading)
        try {
            val token = "Bearer $accToken"
            val storeReparation =
                apiService.storeAlberReparation(token, index, hoursMeter, note)
            emit(Result.Success(storeReparation))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
            Log.i("TAG", "invokeAlberDetail: ${e.message}")
        }
    }

    fun deleteAlber(index: Int) = liveData {
        emit(Result.Loading)
        try {
            val token = "Bearer $accToken"
            val deleteResponse =
                apiService.deleteAlber(token, index)
            emit(Result.Success(deleteResponse))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
            Log.i("TAG", "invokeAlberDetail: ${e.message}")
        }
    }
}