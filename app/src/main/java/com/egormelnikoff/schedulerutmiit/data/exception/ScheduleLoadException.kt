package com.egormelnikoff.schedulerutmiit.data.exception

import com.egormelnikoff.schedulerutmiit.data.TypedError

class ScheduleLoadException(val error: TypedError) : RuntimeException()