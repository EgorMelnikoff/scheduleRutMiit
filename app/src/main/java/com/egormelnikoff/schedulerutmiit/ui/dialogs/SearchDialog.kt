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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomChip
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTextField
import com.egormelnikoff.schedulerutmiit.ui.screens.Empty
import com.egormelnikoff.schedulerutmiit.ui.screens.LoadingScreen
import com.egormelnikoff.schedulerutmiit.ui.view_models.search.SearchUiState

enum class Options {
    ALL, GROUPS, PEOPLE
}

@Composable
fun SearchScheduleDialog(
    externalPadding: PaddingValues,
    onSearch: (Pair<String, Options>) -> Unit,
    onSetDefaultState: () -> Unit,
    onSearchSchedule: (Triple<String, String, Int>) -> Unit,
    onChangeQuery: (String) -> Unit,
    onSelectOption: (Options) -> Unit,

    searchQuery: String,
    selectedOption: Options,
    searchUiState: SearchUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                start = 8.dp,
                end = 8.dp,
                top = externalPadding.calculateTopPadding() + 16.dp,
                bottom = externalPadding.calculateBottomPadding()
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
                value = searchQuery,
                onValueChanged = onChangeQuery,
                action = {
                    onSearch(Pair(searchQuery.trim(), selectedOption))
                },
                placeholderText = LocalContext.current.getString(R.string.search),
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.search_simple),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = searchQuery != "",
                        enter = scaleIn(animationSpec = tween(300)),
                        exit = fadeOut(animationSpec = tween(500))
                    ) {
                        IconButton(
                            onClick = {
                                onChangeQuery("")
                                onSetDefaultState()
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
                selectedOption = selectedOption,
                onSelectOption = onSelectOption
            )
        }
        AnimatedContent(
            modifier = Modifier.fillMaxSize(),
            targetState = searchUiState,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            }
        ) { searchUiState ->
            when {
                searchUiState.isLoading -> LoadingScreen(
                    paddingTop = 0.dp,
                    paddingBottom = 0.dp
                )

                searchUiState.isEmptyQuery -> {
                    Empty(
                        imageVector = ImageVector.vectorResource(R.drawable.search),
                        subtitle = LocalContext.current.getString(R.string.enter_your_query)
                    )
                }

                searchUiState.groups.isEmpty() && searchUiState.people.isEmpty() ->
                    Empty(
                        subtitle = LocalContext.current.getString(R.string.nothing_found)
                    )

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(
                            start = 8.dp,
                            end = 8.dp
                        ),
                        horizontalAlignment = Alignment.Start
                    ) {
                        if (searchUiState.groups.isNotEmpty() && (selectedOption == Options.ALL || selectedOption == Options.GROUPS)) {
                            item {
                                Text(
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    text = LocalContext.current.getString(R.string.groups),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            items(searchUiState.groups) { group ->
                                Box(
                                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                                ) {
                                    ClickableItem(
                                        padding = 8.dp,
                                        title = group.name!!,
                                        onClick = {
                                            onSearchSchedule(
                                                Triple(
                                                    group.name,
                                                    group.id.toString(),
                                                    0
                                                )
                                            )
                                        }
                                    )
                                }

                            }
                        }
                        if (searchUiState.people.isNotEmpty() && (selectedOption == Options.ALL || selectedOption == Options.PEOPLE)) {
                            item {
                                Text(
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    text = LocalContext.current.getString(R.string.people),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            items(searchUiState.people) { person ->
                                Box(
                                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                                ) {
                                    ClickableItem(
                                        padding = 8.dp,
                                        title = person.name!!,
                                        subtitle = person.position!!,
                                        subtitleMaxLines = 3,
                                        imageUrl = "https://www.miit.ru/content/e${person.id}.jpg?id_fe=${person.id}&SWidth=100",
                                        imageUrlErrorTextSize = 20,
                                        imageSize = 60.dp,
                                        onClick = {
                                            onSearchSchedule(
                                                Triple(
                                                    person.name,
                                                    person.id.toString(),
                                                    1
                                                )
                                            )
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
    selectedOption: Options,
    onSelectOption: (Options) -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CustomChip(
            title = LocalContext.current.getString(R.string.all),
            imageVector = null,
            selected = selectedOption == Options.ALL,
            onSelect = {
                onSelectOption(Options.ALL)
            }
        )
        CustomChip(
            title = LocalContext.current.getString(R.string.groups),
            imageVector = ImageVector.vectorResource(R.drawable.group),
            selected = selectedOption == Options.GROUPS,
            onSelect = {
                onSelectOption(Options.GROUPS)
            }
        )
        CustomChip(
            title = LocalContext.current.getString(R.string.people),
            imageVector = ImageVector.vectorResource(R.drawable.person),
            selected = selectedOption == Options.PEOPLE,
            onSelect = {
                onSelectOption(Options.PEOPLE)
            }
        )
    }
}