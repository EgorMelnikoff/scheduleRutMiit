package com.egormelnikoff.schedulerutmiit.core.ui.theme.icons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.egormelnikoff.schedulerutmiit.core.ui.R

@Immutable
class AppIcons internal constructor() {
    val add: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.add)
    val alert: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.alert)
    val back: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.back)
    val calendar: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.calendar)
    val check: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.check)
    val circle: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.circle)
    val clear: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.clear)
    val comment: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.comment)
    val delete: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.delete)
    val deleteAll: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.delete_all)
    val down: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.down)
    val download: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.download)
    val edit: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.edit)
    val email: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.email)
    val eventView: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.event_view)
    val export: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.export)
    val group: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.group)
    val history: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.history)
    val info: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.info)
    val left: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.left)
    val list: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.list)
    val logoApp: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.logo_app)
    val logoGithub: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.logo_github)
    val logoGooglePlay: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.logo_google_play)
    val logoRustore: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.logo_rustore)
    val logoTelegram: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.logo_telegram)
    val moon: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.moon)
    val moreVert: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.more_vert)
    val news: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.news)
    val one: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.one)
    val openPanel: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.open_panel)
    val person: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.person)
    val points: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.points)
    val refresh: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.refresh)
    val resourceImport: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.resource_import)
    val review: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.review)
    val reviewFill: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.review_fill)
    val right: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.right)
    val room: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.room)
    val schedule: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.schedule)
    val scheduleFill: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.schedule_fill)
    val search: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.search)
    val settingsFill: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.settings_fill)
    val share: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.share)
    val split: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.split)
    val sun: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.sun)
    val sync: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.sync)
    val tag: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.tag)
    val tasks: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.tasks)
    val tasksFill: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.tasks_fill)
    val time: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.time)
    val undelete: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.undelete)
    val visibility: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.visibility)
    val visibilityOff: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.visibility_off)
}

val LocalAppIcons = staticCompositionLocalOf { AppIcons() }
