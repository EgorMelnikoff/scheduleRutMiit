package com.egormelnikoff.schedulerutmiit.app.exception

import com.egormelnikoff.schedulerutmiit.data.remote.network.result.TypedError

class ScheduleLoadException(val error: TypedError) : RuntimeException()