package com.egormelnikoff.schedulerutmiit.search.ui.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
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
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.AppCell
import com.egormelnikoff.schedulerutmiit.core.ui.components.cell.LeadingAsyncImage
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.NamedScheduleType
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
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        if (state.groups.isNotEmpty() && (state.searchType == SearchType.ALL || state.searchType == SearchType.GROUPS)) {
            item {
                Text(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = stringResource(R.string.groups),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            items(
                items = state.groups,
                key = { it.id }
            ) { group ->
                Box(
                    modifier = Modifier.clip(MaterialTheme.shapes.medium)
                ) {
                    AppCell(
                        verticalPadding = 8.dp,
                        horizontalPadding = 8.dp,
                        title = group.name
                    ) {
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
                }
            }
            item {
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
        if (state.people.isNotEmpty() && (state.searchType == SearchType.ALL || state.searchType == SearchType.PEOPLE)) {
            item {
                Text(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = stringResource(R.string.people),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            items(
                items = state.people,
                key = { it.id }
            ) { person ->
                Box(
                    modifier = Modifier.clip(MaterialTheme.shapes.medium)
                ) {
                    AppCell(
                        verticalPadding = 8.dp,
                        horizontalPadding = 8.dp,
                        title = person.name,
                        titleMaxLines = 2,
                        subtitle = person.position,
                        subtitleMaxLines = 3,
                        leadingIcon = {
                            LeadingAsyncImage(
                                title = person.name,
                                titleSize = 20.sp,
                                imageUrl = personImageUrl(person.id),
                                imageSize = 60.dp
                            )
                        }
                    ) {
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
                }
            }
        }
    }
}