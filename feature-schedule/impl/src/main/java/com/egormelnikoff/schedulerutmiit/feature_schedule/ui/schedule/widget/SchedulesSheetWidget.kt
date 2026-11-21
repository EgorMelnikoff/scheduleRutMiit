package com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.consts.DateTimeFormatters.dayMonthNameFormatter
import com.egormelnikoff.schedulerutmiit.core.ui.components.SpacerHeight
import com.egormelnikoff.schedulerutmiit.core.ui.components.bottom_sheet.AppBottomSheet
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.CenterContent
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.Icon
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.TextBlock
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.common.LargeIconButton
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.schedule.ScheduleAction
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulesSheetWidget(
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
                text = stringResource(R.string.your_schedules),
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
                icon = ImageVector.vectorResource(R.drawable.resource_import),
                contentDescription = stringResource(R.string.rename)
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
                icon = ImageVector.vectorResource(R.drawable.export),
                contentDescription = stringResource(R.string.rename)
            )
        }

        SpacerHeight(AppTheme.paddings.padding12)

        namedSchedules.forEachIndexed { index, namedSchedule ->
            val locale = LocalLocale.current.platformLocale

            val lastTimeUpdate = remember(
                namedSchedule.lastTimeUpdate
            ) {
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(namedSchedule.lastTimeUpdate),
                    ZoneId.systemDefault()
                ).format(dayMonthNameFormatter.withLocale(locale))

            }

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
                                            title = stringResource(R.string._default),
                                        )
                                    }
                                } else {
                                    null
                                }
                            )
                        },
                        subtitle = {
                            TextBlock(
                                text = "${stringResource(R.string.current_on)} $lastTimeUpdate",
                                typography = AppTheme.typography.bodyMedium,
                                color = AppTheme.colors.onSecondaryContainer,
                            )
                        }
                    )
                },
                end = {
                    if (currentNamedScheduleId == namedSchedule.id) {
                        Icon(
                            icon = ImageVector.vectorResource(R.drawable.check),
                            iconColor = AppTheme.colors.primary
                        )
                    }
                },
                showChevron = currentNamedScheduleId != namedSchedule.id,
                showSeparator = index != namedSchedules.lastIndex,
                onClick = {
                    onAction(ScheduleAction.OnChangeSchedule(namedSchedule.id))
                }
            )
        }
    }
}
