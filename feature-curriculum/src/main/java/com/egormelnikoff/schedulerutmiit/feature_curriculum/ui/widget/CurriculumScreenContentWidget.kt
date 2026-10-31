package com.egormelnikoff.schedulerutmiit.feature_curriculum.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.containers.ScaffoldLazyColumn
import com.egormelnikoff.schedulerutmiit.feature_curriculum.ui.CurriculumState

@Composable
internal fun CurriculumScreenContentWidget(
    contentPadding: PaddingValues,
    state: CurriculumState,
) {
    ScaffoldLazyColumn(
        contentPadding = contentPadding
    ) {
        items(state.subjectsList) { subject ->
            Box(
                modifier = Modifier.clip(MaterialTheme.shapes.medium)
            ) {
                AppCell(
                    verticalPadding = 8.dp,
                    horizontalPadding = 8.dp,
                    title = subject.title,
                    subtitleMaxLines = Int.MAX_VALUE,
                    subtitle = subject.teachers.joinToString(
                        separator = ", \n"
                    ) { it },
                    showClickLabel = false
                )
            }

        }
    }
}