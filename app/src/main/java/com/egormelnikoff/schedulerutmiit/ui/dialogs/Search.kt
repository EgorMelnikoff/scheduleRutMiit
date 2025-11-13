package com.egormelnikoff.schedulerutmiit.ui.dialogs

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomChip
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTextField
import com.egormelnikoff.schedulerutmiit.ui.elements.LeadingAsyncImage
import com.egormelnikoff.schedulerutmiit.ui.navigation.NavigationActions
import com.egormelnikoff.schedulerutmiit.ui.screens.Empty
import com.egormelnikoff.schedulerutmiit.ui.screens.LoadingScreen
import com.egormelnikoff.schedulerutmiit.ui.state.ReviewUiState
import com.egormelnikoff.schedulerutmiit.ui.state.actions.schedule.ScheduleActions
import com.egormelnikoff.schedulerutmiit.ui.state.actions.search.SearchActions
import com.egormelnikoff.schedulerutmiit.view_models.search.SearchState

enum class SearchOption {
    ALL, GROUPS, PEOPLE
}

@Composable
fun SearchDialog(
    searchState: SearchState,
    navigationActions: NavigationActions,
    searchActions: SearchActions,
    scheduleActions: ScheduleActions,
    reviewUiState: ReviewUiState,
    externalPadding: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                start = 8.dp,
                end = 8.dp,
                top = externalPadding.calculateTopPadding() + 16.dp
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
                value = reviewUiState.searchQuery,
                onValueChanged = reviewUiState.onChangeQuery,
                action = {
                    searchActions.onSearchSchedule(Pair(reviewUiState.searchQuery.trim(), reviewUiState.selectedSearchOption))
                },
                placeholderText = LocalContext.current.getString(R.string.search),
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.search),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = reviewUiState.searchQuery != "",
                        enter = scaleIn(animationSpec = tween(300)),
                        exit = fadeOut(animationSpec = tween(500))
                    ) {
                        IconButton(
                            onClick = {
                                reviewUiState.onChangeQuery("")
                                searchActions.onSetDefaultSearchState()
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
            )

            FilterRow(
                selectedOption = reviewUiState.selectedSearchOption,
                onSelectOption = reviewUiState.onSelectSearchOption
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
                searchState.isLoading -> LoadingScreen(
                    paddingTop = 0.dp,
                    paddingBottom = externalPadding.calculateBottomPadding()
                )

                searchState.error != null -> {
                    Empty(
                        subtitle = searchState.error,
                        paddingBottom = externalPadding.calculateBottomPadding()
                    )
                }

                searchState.isEmptyQuery -> {
                    Empty(
                        imageVector = ImageVector.vectorResource(R.drawable.search),
                        subtitle = LocalContext.current.getString(R.string.enter_your_query),
                        paddingBottom = externalPadding.calculateBottomPadding()
                    )
                }

                searchState.groups.isEmpty() && searchState.people.isEmpty() ->
                    Empty(
                        subtitle = LocalContext.current.getString(R.string.nothing_found),
                        paddingBottom = externalPadding.calculateBottomPadding()
                    )

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(
                            start = 8.dp,
                            end = 8.dp,
                            bottom = externalPadding.calculateBottomPadding()
                        ),
                        horizontalAlignment = Alignment.Start
                    ) {
                        if (searchState.groups.isNotEmpty() && (reviewUiState.selectedSearchOption == SearchOption.ALL || reviewUiState.selectedSearchOption == SearchOption.GROUPS)) {
                            item {
                                Text(
                                    text = LocalContext.current.getString(R.string.groups),
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
                                        title = group.name!!,
                                        showClickLabel = false,
                                        onClick = {
                                            navigationActions.navigateToSchedule()
                                            scheduleActions.onGetNamedSchedule(
                                                Triple(
                                                    group.name,
                                                    group.id.toString(),
                                                    0
                                                )
                                            )
                                            searchActions.onSetDefaultSearchState()
                                            reviewUiState.onChangeQuery("")
                                            reviewUiState.onSelectSearchOption(SearchOption.ALL)
                                        }
                                    )
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(2.dp))
                            }
                        }
                        if (searchState.people.isNotEmpty() && (reviewUiState.selectedSearchOption == SearchOption.ALL || reviewUiState.selectedSearchOption == SearchOption.PEOPLE)) {
                            item {
                                Text(
                                    text = LocalContext.current.getString(R.string.people),
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
                                        title = person.name!!,
                                        titleMaxLines = 2,
                                        subtitle = person.position!!,
                                        subtitleMaxLines = 3,
                                        leadingIcon = {
                                            LeadingAsyncImage(
                                                title = person.name,
                                                titleSize = 20.sp,
                                                imageUrl = "https://www.miit.ru/content/e${person.id}.jpg?id_fe=${person.id}&SWidth=100",
                                                imageSize = 60.dp
                                            )
                                        },
                                        showClickLabel = false,
                                        onClick = {
                                            navigationActions.navigateToSchedule()
                                            scheduleActions.onGetNamedSchedule(
                                                Triple(
                                                    person.name,
                                                    person.id.toString(),
                                                    1
                                                )
                                            )
                                            searchActions.onSetDefaultSearchState()
                                            reviewUiState.onChangeQuery("")
                                            reviewUiState.onSelectSearchOption(SearchOption.ALL)
                                        }
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

@Composable
fun FilterRow(
    selectedOption: SearchOption,
    onSelectOption: (SearchOption) -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CustomChip(
            title = LocalContext.current.getString(R.string.all),
            imageVector = null,
            selected = selectedOption == SearchOption.ALL,
            onSelect = {
                onSelectOption(SearchOption.ALL)
            }
        )
        CustomChip(
            title = LocalContext.current.getString(R.string.groups),
            imageVector = ImageVector.vectorResource(R.drawable.group),
            selected = selectedOption == SearchOption.GROUPS,
            onSelect = {
                onSelectOption(SearchOption.GROUPS)
            }
        )
        CustomChip(
            title = LocalContext.current.getString(R.string.people),
            imageVector = ImageVector.vectorResource(R.drawable.person),
            selected = selectedOption == SearchOption.PEOPLE,
            onSelect = {
                onSelectOption(SearchOption.PEOPLE)
            }
        )
    }
}