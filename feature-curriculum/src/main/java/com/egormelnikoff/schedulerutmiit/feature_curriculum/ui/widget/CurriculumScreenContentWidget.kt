package com.egormelnikoff.schedulerutmiit.feature_curriculum.ui.widget

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.CenterContent
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.TextBlock
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ScaffoldLazyColumn
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.feature_curriculum.ui.CurriculumState

@Composable
internal fun CurriculumScreenContentWidget(
    state: CurriculumState,
) {
    ScaffoldLazyColumn(
        contentPadding = PaddingValues(
            horizontal = AppTheme.paddings.padding16
        ),
        itemSpacing = AppTheme.paddings.padding16
    ) {
        items(state.subjectsList) { subject ->
            AppCell(
                contentPadding = PaddingValues(
                    AppTheme.paddings.none
                ),
                center = {
                    CenterContent(
                        title = {
                            TextBlock(
                                text = subject.title,
                            )
                        },
                        subtitle = {
                            TextBlock(
                                text = subject.teachers.joinToString(
                                    separator = ", \n"
                                ) { it },
                                typography = AppTheme.typography.bodyMedium,
                                color = AppTheme.colors.onSecondaryContainer,
                                maxLines = Int.MAX_VALUE,
                            )
                        },
                    )
                },
                showChevron = false
            )

        }
    }
}