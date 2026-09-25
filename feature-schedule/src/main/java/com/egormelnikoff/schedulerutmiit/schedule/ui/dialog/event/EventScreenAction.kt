package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.event

import com.egormelnikoff.schedulerutmiit.core.common.domain.Group
import com.egormelnikoff.schedulerutmiit.core.common.domain.Lecturer

sealed interface EventScreenAction {
    data object OnBackClicked : EventScreenAction

    data class OnEditEventClicked(
        val eventId: Long,
        val scheduleId: Long
    ) : EventScreenAction

    data class OnDeleteEvent(
        val eventId: Long
    ) : EventScreenAction

    data class OnHideEvent(
        val eventId: Long
    ) : EventScreenAction

    data class OnShowDeleteDialog(
        val value: Boolean
    ) : EventScreenAction

    data class OnShowHideDialog(
        val value: Boolean
    ) : EventScreenAction

    data class OnShowMoreDialog(
        val value: Boolean
    ) : EventScreenAction

    data class OnSendClicked(
        val text: String
    ) : EventScreenAction


    data class OnCommentChanged(
        val comment: String
    ) : EventScreenAction

    data class OnTagChanged(
        val tag: Int
    ) : EventScreenAction

    data class OnGroupClicked(
        val group: Group
    ) : EventScreenAction

    data class OnGroupLongClicked(
        val group: Group
    ) : EventScreenAction

    data class OnLecturerClicked(
        val lecturer: Lecturer
    ) : EventScreenAction

    data class OnLecturerLongClicked(
        val lecturer: Lecturer
    ) : EventScreenAction
}