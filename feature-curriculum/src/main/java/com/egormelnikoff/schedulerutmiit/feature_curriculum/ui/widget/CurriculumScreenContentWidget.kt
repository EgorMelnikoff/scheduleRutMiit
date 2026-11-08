package com.egormelnikoff.schedulerutmiit.feature_curriculum.ui.widget

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
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
                title = subject.title,
                verticalPadding = AppTheme.paddings.none,
                horizontalPadding = AppTheme.paddings.none,
                subtitleMaxLines = Int.MAX_VALUE,
                subtitle = subject.teachers.joinToString(
                    separator = ", \n"
                ) { it },
                showClickLabel = false
            )

        }
    }
}