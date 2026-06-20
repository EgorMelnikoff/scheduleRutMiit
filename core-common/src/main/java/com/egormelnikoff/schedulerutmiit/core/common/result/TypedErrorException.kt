package com.egormelnikoff.schedulerutmiit.core.common.result

class TypedErrorException(
    val typedError: TypedError
) : Throwable()