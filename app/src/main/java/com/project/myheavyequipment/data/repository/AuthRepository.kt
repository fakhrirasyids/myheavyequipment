package com.project.myheavyequipment.data.repository

import androidx.lifecycle.liveData
import com.project.myheavyequipment.utils.Result
import com.project.myheavyequipment.data.remote.ApiService

class AuthRepository(
    private val accToken: String?,
    private val apiService: ApiService
) {
    fun register(
        name: String,
        email: String,
        password: String,
    ) = liveData {
        emit(Result.Loading)
        try {
            val registerResponse = apiService.register(name, email, password)
            emit(Result.Success(registerResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }

    fun login(email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val loginResponse = apiService.login(email, password)
            emit(Result.Success(loginResponse))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }

    fun logout() = liveData {
        emit(Result.Loading)
        try {
            val token = "Bearer $accToken"
            val logoutResponse = apiService.logout(token)
            emit(Result.Success(logoutResponse))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }
}