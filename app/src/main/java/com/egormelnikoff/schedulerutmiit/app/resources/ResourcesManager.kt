package com.egormelnikoff.schedulerutmiit.app.resources

import android.content.Context
import javax.inject.Inject

class ResourcesManager @Inject constructor(
    private val context: Context
) {
    fun getString(id: Int) = context.getString(id)
    //fun getDrawable(id: Int) = context.getDrawable(id)
}