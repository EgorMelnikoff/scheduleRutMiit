package com.egormelnikoff.schedulerutmiit.app.exception

import com.egormelnikoff.schedulerutmiit.data.TypedError

class ScheduleLoadException(val error: TypedError) : RuntimeException()