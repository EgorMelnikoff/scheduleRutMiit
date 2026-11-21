package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event

import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Group
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Lecturer
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Room

sealed interface EventAction {
    data object OnBackClicked : EventAction

    data class OnEditEventClicked(
        val eventId: Long,
        val scheduleId: Long
    ) : EventAction

    data class OnDeleteEvent(
        val eventId: Long
    ) : EventAction

    data class OnHideEvent(
        val eventId: Long
    ) : EventAction

    data class OnShowDeleteDialog(
        val value: Boolean
    ) : EventAction

    data class OnShowHideDialog(
        val value: Boolean
    ) : EventAction

    data class OnShowMoreDialog(
        val value: Boolean
    ) : EventAction

    data class OnSendClicked(
        val text: String
    ) : EventAction


    data class OnCommentChanged(
        val comment: String
    ) : EventAction

    data class OnTagChanged(
        val tag: Int
    ) : EventAction

    data class OnGroupClicked(
        val group: Group
    ) : EventAction

    data class OnGroupLongClicked(
        val group: Group
    ) : EventAction

    data class OnRoomClicked(
        val room: Room
    ) : EventAction

    data class OnRoomLongClicked(
        val room: Room
    ) : EventAction

    data class OnLecturerClicked(
        val lecturer: Lecturer
    ) : EventAction

    data class OnLecturerLongClicked(
        val lecturer: Lecturer
    ) : EventAction
}