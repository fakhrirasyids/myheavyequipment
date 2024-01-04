package com.project.myheavyequipment.utils

sealed class Result<out R> private constructor() {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val error: String) : Result<Nothing>()
    data class ErrorFirstFetch(val error: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}