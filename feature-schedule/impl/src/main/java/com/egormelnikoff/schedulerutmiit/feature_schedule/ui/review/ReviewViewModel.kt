package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.review

import androidx.lifecycle.viewModelScope
import com.egormelnikoff.schedulerutmiit.core.common.time_provider.TimeProvider
import com.egormelnikoff.schedulerutmiit.core.navigation.AppRouter
import com.egormelnikoff.schedulerutmiit.core.navigation.model.Route
import com.egormelnikoff.schedulerutmiit.core.settings.api.repos.SettingsRepos
import com.egormelnikoff.schedulerutmiit.core.ui.view_model.BaseViewModel
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.use_case.DeleteNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.use_case.SetDefaultNamedScheduleUseCase
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.use_case.observer.ObserveSummaryUseCase
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.utils.dayPeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val observeSummaryUseCase: ObserveSummaryUseCase,
    private val timeProvider: TimeProvider,

    private val setDefaultNamedScheduleUseCase: SetDefaultNamedScheduleUseCase,
    private val deleteNamedScheduleUseCase: DeleteNamedScheduleUseCase,

    private val appRouter: AppRouter,
    private val namedScheduleRepos: NamedScheduleRepos,
    private val settingsRepos: SettingsRepos
) : BaseViewModel<ReviewState, ReviewState, ReviewAction>(ReviewState()) {

    override fun onScreenInitialized() {
        super.onScreenInitialized()
        namedScheduleRepos.observeAll().onEach { schedules ->
            updateState {
                copy(namedSchedules = schedules)
            }
        }
            .launchIn(viewModelScope)

        timeProvider.currentHour
            .onEach { dateTime ->
                updateState {
                    copy(
                        currentDate = dateTime.toLocalDate(),
                        dayPeriod = dateTime.dayPeriod()
                    )
                }
            }
            .launchIn(viewModelScope)

        observeSummaryUseCase()
            .onEach { summary ->
                updateState {
                    copy(summaryState = summary)
                }
            }
            .launchIn(viewModelScope)

        settingsRepos.observeReviewSettings()
            .onEach { reviewSettings ->
                updateState {
                    copy(
                        theme = reviewSettings.theme,
                        usedPhoto = reviewSettings.usedImageInReview
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: ReviewAction) {
        when (action) {
            ReviewAction.OnCreateScheduleClicked -> onCreateScheduleClicked()
            ReviewAction.OnFindScheduleClicked -> onFindScheduleClicked()
            ReviewAction.OnCurriculumClicked -> onCurriculumClicked()
            ReviewAction.OnTasksClicked -> onTasksClicked()
            ReviewAction.OnNewsCLicked -> onNewsCLicked()
            is ReviewAction.OnRenameScheduleClicked -> onRenameScheduleClicked(action.namedScheduleId)

            is ReviewAction.OnSetNamedSchedule -> onSetNamedSchedule(
                action.namedScheduleId,
                action.setDefault
            )

            is ReviewAction.OnDeleteNamedSchedule -> onDeleteNamedSchedule(
                action.id,
                action.isDefault
            )

            is ReviewAction.OnShowSchedules -> updateState {
                copy(showSchedules = action.show)
            }

            is ReviewAction.OnShowServices -> updateState {
                copy(showServices = action.show)
            }

            is ReviewAction.OnShowDeleteScheduleSheet -> updateState {
                if (action.namedSchedule != null) {
                    copy(
                        scheduleDeleteSheet = action.namedSchedule,
                        scheduleSheet = null
                    )
                } else {
                    copy(scheduleDeleteSheet = null)
                }
            }

            is ReviewAction.OnShowScheduleSheet -> updateState {
                if (action.namedSchedule != null) {
                    copy(
                        scheduleSheet = action.namedSchedule,
                        scheduleDeleteSheet = null
                    )
                } else {
                    copy(scheduleSheet = null)
                }
            }
        }
    }

    private fun onCreateScheduleClicked() {
        appRouter.openDialog(Route.Dialog.AddScheduleDialog)
    }

    private fun onFindScheduleClicked() {
        appRouter.openDialog(Route.Dialog.SearchDialog)
    }

    private fun onCurriculumClicked() {
        appRouter.openDialog(Route.Dialog.CurriculumDialog)
    }

    private fun onTasksClicked() {
        appRouter.openPage(Route.Page.Tasks)
    }

    private fun onNewsCLicked() {
        appRouter.openDialog(Route.Dialog.NewsList)
    }

    private fun onRenameScheduleClicked(namedScheduleId: Long) {
        appRouter.openDialog(Route.Dialog.RenameNamedScheduleDialog(namedScheduleId))
    }

    private fun onSetNamedSchedule(
        namedScheduleId: Long,
        setDefault: Boolean = false,
    ) {
        viewModelScope.launch {
            if (setDefault) {
                setDefaultNamedScheduleUseCase(namedScheduleId)
                namedScheduleRepos.openDefault()
            } else {
                namedScheduleRepos.getAndOpen(namedScheduleId)
                appRouter.navigateToStartRage()
            }

            updateState {
                copy(
                    scheduleSheet = null
                )
            }

        }
    }

    private fun onDeleteNamedSchedule(
        id: Long,
        isDefault: Boolean
    ) {
        viewModelScope.launch {
            namedScheduleRepos.openDefault()

            deleteNamedScheduleUseCase(
                id, isDefault
            )
        }
    }
}