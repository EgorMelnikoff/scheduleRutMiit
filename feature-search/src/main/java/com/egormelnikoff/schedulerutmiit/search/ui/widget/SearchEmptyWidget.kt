package com.egormelnikoff.schedulerutmiit.search.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.Icon
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.IconButton
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.TextBlock
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
                    contentPadding = PaddingValues(
                        AppTheme.paddings.padding8
                    ),
                    start = {
                        Icon(
                            icon = ImageVector.vectorResource(R.drawable.history),
                            iconSize = 20.dp,
                            iconColor = AppTheme.colors.onSecondaryContainer
                        )
                    },
                    center = {
                        TextBlock(
                            text = query.name,
                            maxLines = 2,
                        )
                    },
                    end = {
                        IconButton(
                            icon = ImageVector.vectorResource(R.drawable.clear),
                            iconButtonColors = IconButtonDefaults.iconButtonColors().copy(
                                contentColor = AppTheme.colors.onBackground
                            )
                        ) {
                            onAction(
                                SearchAction.OnDeleteQuery(
                                    queryId = query.id
                                )
                            )
                        }
                    },

                    showChevron = false,
                    onClick = {
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
                )
            }
        }
    }
}