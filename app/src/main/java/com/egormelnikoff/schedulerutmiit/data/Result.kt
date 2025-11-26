package com.egormelnikoff.schedulerutmiit.data

sealed interface Result<out R> {
    data class Success<S>(
        val data: S
    ) : Result<S>

    data class Error(
        val typedError: TypedError
    ) : Result<Nothing>
}