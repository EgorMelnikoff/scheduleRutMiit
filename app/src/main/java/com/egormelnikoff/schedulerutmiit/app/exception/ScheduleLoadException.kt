package com.egormelnikoff.schedulerutmiit.app.exception

import com.egormelnikoff.schedulerutmiit.app.network.result.TypedError

class ScheduleLoadException(val error: TypedError) : RuntimeException()