package com.egormelnikoff.schedulerutmiit.search.ui.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints.personImageUrl
import com.egormelnikoff.schedulerutmiit.core.ui.components.SpacerHeight
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AsyncImage
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.CenterContent
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.TextBlock
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.feature_schedule.api.model.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.search.domain.model.RefreshArgs
import com.egormelnikoff.schedulerutmiit.search.ui.SearchAction
import com.egormelnikoff.schedulerutmiit.search.ui.SearchState
import com.egormelnikoff.schedulerutmiit.search.utils.SearchType

@Composable
internal fun SearchContentWidget(
    state: SearchState,
    contentPadding: PaddingValues,
    onAction: (SearchAction) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = contentPadding.calculateBottomPadding()),
        verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding8),
        horizontalAlignment = Alignment.Start
    ) {
        if (state.groups.isNotEmpty() && (state.searchType == SearchType.ALL || state.searchType == SearchType.GROUPS)) {
            item {
                Text(
                    modifier = Modifier.padding(horizontal = AppTheme.paddings.padding8),
                    text = stringResource(R.string.groups),
                    style = AppTheme.typography.titleSmall,
                    color = AppTheme.colors.onSecondaryContainer
                )
            }
            items(
                items = state.groups,
                key = { it.id }
            ) { group ->
                Box(
                    modifier = Modifier.clip(AppTheme.shapes.medium)
                ) {
                    AppCell(
                        contentPadding = PaddingValues(
                            AppTheme.paddings.padding8
                        ),
                        center = {
                            TextBlock(
                                text = group.name
                            )
                        },
                        onClick = {
                            onAction(
                                SearchAction.OnNavigateSchedule(
                                    RefreshArgs(
                                        group.name,
                                        group.id,
                                        NamedScheduleType.GROUP
                                    )
                                )
                            )
                        }
                    )
                }
            }
            item {
                SpacerHeight(AppTheme.paddings.padding2)
            }
        }
        if (state.people.isNotEmpty() && (state.searchType == SearchType.ALL || state.searchType == SearchType.PEOPLE)) {
            item {
                Text(
                    modifier = Modifier.padding(horizontal = AppTheme.paddings.padding8),
                    text = stringResource(R.string.people),
                    style = AppTheme.typography.titleSmall,
                    color = AppTheme.colors.onSecondaryContainer
                )
            }
            items(
                items = state.people,
                key = { it.id }
            ) { person ->
                Box(
                    modifier = Modifier.clip(AppTheme.shapes.medium)
                ) {
                    AppCell(
                        contentPadding = PaddingValues(
                            AppTheme.paddings.padding8
                        ),
                        start = {
                            AsyncImage(
                                imageUrl = personImageUrl(person.id),
                                imageTitle = person.name,
                                titleSize = 20.sp,
                                imageSize = 60.dp
                            )
                        },
                        center = {
                            CenterContent(
                                title = {
                                    TextBlock(
                                        text = person.name,
                                        maxLines = 2,
                                    )
                                },
                                subtitle = {
                                    TextBlock(
                                        text = person.position,
                                        maxLines = Int.MAX_VALUE,
                                        typography = AppTheme.typography.bodyMedium,
                                        color = AppTheme.colors.onSecondaryContainer,
                                    )
                                },
                            )
                        },
                        onClick = {
                            onAction(
                                SearchAction.OnNavigateSchedule(
                                    RefreshArgs(
                                        person.name,
                                        person.id,
                                        NamedScheduleType.PERSON
                                    )
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}