package com.egormelnikoff.schedulerutmiit.data.datasource.resources

import android.content.Context
import android.graphics.drawable.Drawable

interface ResourcesManager{
    fun getString(id: Int): String?
    fun getDrawable(id: Int): Drawable?
}

class ResourcesManagerImpl (private  val context: Context): ResourcesManager {
    override fun getString(id: Int) = context.getString(id)
    override fun getDrawable(id: Int) = context.getDrawable(id)
}