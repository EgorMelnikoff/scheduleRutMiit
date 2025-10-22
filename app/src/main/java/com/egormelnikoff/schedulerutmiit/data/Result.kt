package com.egormelnikoff.schedulerutmiit.data

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(
        val error: com.egormelnikoff.schedulerutmiit.data.Error
    ) : Result<Nothing>()
}