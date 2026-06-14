package com.egormelnikoff.schedulerutmiit.ui.dialog.search

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.core.common.enums.SearchType
import com.egormelnikoff.schedulerutmiit.core.database.entity.SearchQuery
import com.egormelnikoff.schedulerutmiit.core.network.endpoins.Endpoints.personImageUrl
import com.egormelnikoff.schedulerutmiit.core.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomTextField
import com.egormelnikoff.schedulerutmiit.core.ui.elements.LeadingAsyncImage
import com.egormelnikoff.schedulerutmiit.core.ui.elements.composable.Empty
import com.egormelnikoff.schedulerutmiit.core.ui.elements.composable.LoadingScreen
import com.egormelnikoff.schedulerutmiit.search.ui.FilterRow
import com.egormelnikoff.schedulerutmiit.search.ui.view_model.SearchViewModel

@Composable
fun SearchDialog(
    fetchNamedSchedule: (String, Int, NamedScheduleType) -> Unit
) {
    val searchViewModel = hiltViewModel<SearchViewModel>()

    val searchParams =
        searchViewModel.searchParams.collectAsStateWithLifecycle().value
    val searchState =
        searchViewModel.searchState.collectAsStateWithLifecycle().value


    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(
                    start = 8.dp,
                    end = 8.dp,
                    top = innerPadding.calculateTopPadding() + 16.dp
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    value = searchParams.query,
                    placeholderText = "${stringResource(R.string.for_example)}, " +
                            when (searchParams.searchType) {
                                SearchType.ALL ->
                                    stringResource(R.string.example_group) +
                                            " ${stringResource(R.string.or)} " +
                                            stringResource(R.string.example_lecturer)

                                SearchType.GROUPS -> stringResource(R.string.example_group)

                                SearchType.PEOPLE -> stringResource(R.string.example_lecturer)
                            },
                    leadingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.search),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    },
                    trailingIcon = {
                        AnimatedVisibility(
                            visible = !searchState.isEmptyQuery || searchParams.query.isNotEmpty(),
                            enter = scaleIn(animationSpec = tween(300)),
                            exit = fadeOut(animationSpec = tween(500))
                        ) {
                            IconButton(
                                onClick = {
                                    searchViewModel.setDefaultSearchState()
                                }
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.clear),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false,
                        imeAction = ImeAction.Search
                    )
                ) { newValue ->
                    searchViewModel.changeSearchParams(query = newValue)
                }

                FilterRow(
                    selectedOption = searchParams.searchType,
                    onSelectOption = { searchType ->
                        searchViewModel.changeSearchParams(searchType = searchType)
                    }
                )
            }
            AnimatedContent(
                modifier = Modifier.fillMaxSize(),
                targetState = searchState,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                }
            ) { searchState ->
                when {
                    searchState.isLoading -> LoadingScreen()

                    searchState.error != null -> {
                        Empty(
                            subtitle = searchState.error
                        )
                    }

                    searchState.isEmptyQuery && searchState.history.isEmpty() -> {
                        Empty(
                            imageVector = ImageVector.vectorResource(R.drawable.search),
                            subtitle = stringResource(R.string.enter_your_query)
                        )
                    }

                    searchState.isEmptyQuery -> {
                        Column(
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        ) {
                            searchState.history.forEach { query ->
                                Box(
                                    modifier = Modifier.clip(MaterialTheme.shapes.medium)
                                ) {
                                    ClickableItem(
                                        verticalPadding = 8.dp,
                                        horizontalPadding = 8.dp,
                                        title = query.name,
                                        leadingIcon = {
                                            Icon(
                                                imageVector = ImageVector.vectorResource(R.drawable.history),
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        },
                                        trailingIcon = {
                                            IconButton(
                                                modifier = Modifier.size(36.dp),
                                                onClick = {
                                                    searchViewModel.deleteQueryFromHistory(
                                                        query.id
                                                    )
                                                }
                                            ) {
                                                Icon(
                                                    modifier = Modifier.size(24.dp),
                                                    imageVector = ImageVector.vectorResource(R.drawable.clear),
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onBackground
                                                )
                                            }
                                        },
                                        titleMaxLines = 2,
                                        showClickLabel = false
                                    ) {
                                        fetchNamedSchedule(
                                            query.name,
                                            query.apiId,
                                            query.namedScheduleType
                                        )
                                    }
                                }
                            }
                        }
                    }

                    searchState.groups.isEmpty() && searchState.people.isEmpty() ->
                        Empty(
                            subtitle = stringResource(R.string.nothing_found)
                        )

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding()),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            if (searchState.groups.isNotEmpty() && (searchParams.searchType == SearchType.ALL || searchParams.searchType == SearchType.GROUPS)) {
                                item {
                                    Text(
                                        modifier = Modifier.padding(horizontal = 8.dp),
                                        text = stringResource(R.string.groups),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                                items(searchState.groups) { group ->
                                    Box(
                                        modifier = Modifier.clip(MaterialTheme.shapes.medium)
                                    ) {
                                        ClickableItem(
                                            verticalPadding = 8.dp,
                                            horizontalPadding = 8.dp,
                                            title = group.name
                                        ) {
                                            fetchNamedSchedule(
                                                group.name,
                                                group.id,
                                                NamedScheduleType.GROUP
                                            )
                                            searchViewModel.saveQueryToHistory(
                                                SearchQuery(
                                                    name = group.name,
                                                    apiId = group.id,
                                                    namedScheduleType = NamedScheduleType.GROUP
                                                )
                                            )
                                        }
                                    }
                                }
                                item {
                                    Spacer(modifier = Modifier.height(2.dp))
                                }
                            }
                            if (searchState.people.isNotEmpty() && (searchParams.searchType == SearchType.ALL || searchParams.searchType == SearchType.PEOPLE)) {
                                item {
                                    Text(
                                        modifier = Modifier.padding(horizontal = 8.dp),
                                        text = stringResource(R.string.people),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                                items(searchState.people) { person ->
                                    Box(
                                        modifier = Modifier.clip(MaterialTheme.shapes.medium)
                                    ) {
                                        ClickableItem(
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
                                            fetchNamedSchedule(
                                                person.name,
                                                person.id,
                                                NamedScheduleType.PERSON
                                            )
                                            searchViewModel.saveQueryToHistory(
                                                SearchQuery(
                                                    name = person.name,
                                                    apiId = person.id,
                                                    namedScheduleType = NamedScheduleType.PERSON
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}