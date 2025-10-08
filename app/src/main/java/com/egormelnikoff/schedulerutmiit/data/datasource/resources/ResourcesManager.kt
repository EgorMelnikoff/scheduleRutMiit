package com.egormelnikoff.schedulerutmiit.data.datasource.resources

import android.content.Context
import android.graphics.drawable.Drawable
import javax.inject.Inject

interface ResourcesManager {
    fun getString(id: Int): String?
    fun getDrawable(id: Int): Drawable?
}

class ResourcesManagerImpl @Inject constructor(
    private val context: Context
) : ResourcesManager {
    override fun getString(id: Int) = context.getString(id)
    override fun getDrawable(id: Int) = context.getDrawable(id)
}