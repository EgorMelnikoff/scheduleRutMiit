package com.egormelnikoff.schedulerutmiit.core.common.exception


import com.egormelnikoff.schedulerutmiit.core.common.result.TypedError

class ScheduleLoadException(val error: TypedError) : RuntimeException()