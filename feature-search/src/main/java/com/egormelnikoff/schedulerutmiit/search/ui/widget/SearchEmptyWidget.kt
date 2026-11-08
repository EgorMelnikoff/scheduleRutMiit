package com.egormelnikoff.schedulerutmiit.search.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.search.domain.model.RefreshArgs
import com.egormelnikoff.schedulerutmiit.search.domain.model.SearchQuery
import com.egormelnikoff.schedulerutmiit.search.ui.SearchAction

@Composable
internal fun SearchEmptyWidget(
    history: List<SearchQuery>,
    onAction: (SearchAction) -> Unit
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        history.forEach { query ->
            Box(
                modifier = Modifier.clip(AppTheme.shapes.medium)
            ) {
                AppCell(
                    verticalPadding = 8.dp,
                    horizontalPadding = 8.dp,
                    title = query.name,
                    leadingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.history),
                            contentDescription = null,
                            tint = AppTheme.colors.onSecondaryContainer
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            modifier = Modifier.size(36.dp),
                            onClick = {
                                onAction(
                                    SearchAction.OnDeleteQuery(
                                        queryId = query.id
                                    )
                                )
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = ImageVector.vectorResource(R.drawable.clear),
                                contentDescription = null,
                                tint = AppTheme.colors.onBackground
                            )
                        }
                    },
                    titleMaxLines = 2,
                    showClickLabel = false
                ) {
                    onAction(
                        SearchAction.OnNavigateSchedule(
                            RefreshArgs(
                                query.name,
                                query.apiId,
                                query.namedScheduleType
                            )
                        )
                    )
                }
            }
        }
    }
}