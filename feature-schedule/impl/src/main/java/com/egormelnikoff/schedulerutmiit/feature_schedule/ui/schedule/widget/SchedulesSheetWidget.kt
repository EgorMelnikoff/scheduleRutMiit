package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.egormelnikoff.schedulerutmiit.core.common.consts.DateTimeFormatters.dayMonthNameFormatter
import com.egormelnikoff.schedulerutmiit.core.ui.components.SpacerHeight
import com.egormelnikoff.schedulerutmiit.core.ui.components.bottom_sheet.AppBottomSheet
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AnimatedIcon
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.CenterContent
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.TextBlock
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.R
import com.egormelnikoff.schedulerutmiit.feature_schedule.api.model.ScheduleType
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.common.LargeIconButton
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.ScheduleAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SchedulesSheetWidget(
    currentNamedScheduleId: Long? = null,
    namedSchedules: List<NamedSchedule>,
    onAction: (ScheduleAction) -> Unit,
    onExportClick: () -> Unit
) {
    AppBottomSheet(
        showHandle = false,
        verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.none),
        onDismiss = {
            onAction(ScheduleAction.OnDismissSchedulesSheet)
        }
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = AppTheme.paddings.padding16
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding8)
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.schedule_your_schedules),
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colors.onBackground,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            LargeIconButton(
                onClick = {
                    onAction(ScheduleAction.OnDismissSchedulesSheet)
                    onAction(ScheduleAction.OnImportClicked)
                },
                colors = IconButtonDefaults.iconButtonColors().copy(
                    containerColor = AppTheme.colors.secondaryContainer,
                    contentColor = AppTheme.colors.onBackground
                ),
                icon = AppTheme.icons.resourceImport,
                contentDescription = stringResource(R.string.schedule_rename)
            )

            LargeIconButton(
                onClick = {
                    onAction(ScheduleAction.OnDismissSchedulesSheet)
                    onExportClick()
                },
                colors = IconButtonDefaults.iconButtonColors().copy(
                    containerColor = AppTheme.colors.secondaryContainer,
                    contentColor = AppTheme.colors.onBackground
                ),
                icon = AppTheme.icons.export,
                contentDescription = stringResource(R.string.schedule_rename)
            )
        }

        SpacerHeight(AppTheme.paddings.padding12)

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
         ) {
            val locale = LocalLocale.current.platformLocale

            namedSchedules.forEachIndexed { index, namedSchedule ->
                AppCell(
                    contentPadding = PaddingValues(
                        horizontal = AppTheme.paddings.padding16,
                        vertical = AppTheme.paddings.padding16
                    ),
                    center = {
                        CenterContent(
                            title = {
                                TextBlock(
                                    text = namedSchedule.shortName,
                                    label = if (namedSchedule.isDefault) {
                                        {
                                            BadgeWidget(
                                                title = stringResource(R.string.schedule_default),
                                            )
                                        }
                                    } else {
                                        null
                                    }
                                )
                            },
                            subtitle = if (namedSchedule.type != ScheduleType.MY){
                                {
                                    TextBlock(
                                        text = "${stringResource(R.string.schedule_current_on)} " +
                                                "${namedSchedule.lastTimeUpdate.format(dayMonthNameFormatter.withLocale(locale))}",
                                        typography = AppTheme.typography.bodyMedium,
                                        color = AppTheme.colors.onSecondaryContainer,
                                    )
                                }
                            } else {
                                null
                            }
                        )
                    },
                    end = {
                        val isCurrent = currentNamedScheduleId == namedSchedule.id
                        AnimatedIcon(
                            targetState = currentNamedScheduleId == namedSchedule.id,
                            color = if (isCurrent) {
                                AppTheme.colors.primary
                            } else {
                                AppTheme.colors.onSecondaryContainer
                            }
                        ) { state ->
                           if (state) {
                               AppTheme.icons.check
                           } else {
                               AppTheme.icons.right
                           }
                        }
                    },
                    showChevron = false,
                    showSeparator = index != namedSchedules.lastIndex,
                    onClick = {
                        onAction(ScheduleAction.OnChangeSchedule(namedSchedule.id))
                    }
                )
            }
        }
    }
}
