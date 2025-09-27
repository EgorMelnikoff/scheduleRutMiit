package com.egormelnikoff.schedulerutmiit.ui.screens.search

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.data.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomChip
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTextField
import com.egormelnikoff.schedulerutmiit.ui.screens.Empty
import com.egormelnikoff.schedulerutmiit.ui.screens.LoadingScreen
import com.egormelnikoff.schedulerutmiit.ui.view_models.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.ui.view_models.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.ui.view_models.SearchUiState
import com.egormelnikoff.schedulerutmiit.ui.view_models.SearchViewModel

enum class Options {
    ALL, GROUPS, PEOPLE
}

@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
    scheduleViewModel: ScheduleViewModel,
    searchUiState: SearchUiState,
    navigateToSchedule: () -> Unit,
    navigateToAddSchedule: () -> Unit,
    scheduleUiState: ScheduleUiState,
    selectedOption: Options,
    onSelectOption: (Options) -> Unit,
    query: String,
    onQueryChanged: (String) -> Unit,
    paddingValues: PaddingValues,
    namedScheduleActionsDialog: NamedScheduleEntity?,
    onShowActionsDialog: (NamedScheduleEntity?) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(top = paddingValues.calculateTopPadding() + 16.dp)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CustomTextField(
                modifier = Modifier.weight(1f),
                maxLines = 1,
                value = query,
                onValueChanged = onQueryChanged,
                action = {
                    searchViewModel.search(query.trim(), selectedOption)
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
                        visible = query != "",
                        enter = scaleIn(animationSpec = tween(300)),
                        exit = fadeOut(animationSpec = tween(500))
                    ) {
                        IconButton(
                            onClick = {
                                onQueryChanged("")
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
            )
            IconButton(
                onClick = {
                    navigateToAddSchedule()
                },
                colors = IconButtonDefaults.iconButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    modifier = Modifier
                        .size(24.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.add),
                    contentDescription = null
                )
            }
        }
        FilterRow(
            selectedOption = selectedOption,
            onSelectOption = onSelectOption
        )
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
                    paddingBottom = paddingValues.calculateBottomPadding()
                )

                searchUiState.isEmptyQuery -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                bottom = paddingValues.calculateBottomPadding()
                            )
                    ) {
                        if (scheduleUiState.savedNamedSchedules.isEmpty()) {
                            Empty(
                                imageVector = ImageVector.vectorResource(R.drawable.search),
                                subtitle = LocalContext.current.getString(R.string.enter_your_query),
                            )
                        } else {
                            SavedSchedules(
                                scheduleUiState = scheduleUiState,
                                namedScheduleActionsDialog = namedScheduleActionsDialog,
                                onShowActionsDialog = onShowActionsDialog,
                                scheduleViewModel = scheduleViewModel,
                                navigateToSchedule = navigateToSchedule
                            )
                        }
                    }
                }

                searchUiState.groups.isEmpty() && searchUiState.people.isEmpty() -> Empty(
                    subtitle = LocalContext.current.getString(R.string.nothing_found),
                    paddingBottom = paddingValues.calculateBottomPadding()
                )

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(
                            start = 8.dp,
                            end = 8.dp,
                            bottom = paddingValues.calculateBottomPadding()
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
                                SearchedItem(
                                    onClick = {
                                        navigateToSchedule()
                                        scheduleViewModel.getNamedScheduleFromApi(
                                            name = group.name!!,
                                            apiId = group.id.toString(),
                                            type = 0
                                        )
                                        onQueryChanged("")
                                        searchViewModel.setDefaultSearchState()
                                    },
                                    title = group.name!!
                                )
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
                                SearchedItem(
                                    onClick = {
                                        navigateToSchedule()
                                        scheduleViewModel.getNamedScheduleFromApi(
                                            name = person.name!!,
                                            apiId = person.id.toString(),
                                            type = 1
                                        )
                                        onQueryChanged("")
                                        searchViewModel.setDefaultSearchState()
                                    },
                                    title = person.name!!,
                                    subtitle = person.position!!,
                                    imageUrl = "https://www.miit.ru/content/e${person.id}.jpg?id_fe=${person.id}&SWidth=100"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterRow (
    selectedOption: Options,
    onSelectOption: (Options) -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(
            modifier = Modifier.width(8.dp)
        )
        CustomChip (
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
        Spacer(
            modifier = Modifier.width(8.dp)
        )
    }
}

@Composable
fun SearchedItem(
    onClick: () -> Unit,
    title: String,
    subtitle: String? = null,
    imageUrl: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (imageUrl != null) {
            val model = rememberAsyncImagePainter(imageUrl)
            val transition by animateFloatAsState(
                targetValue = if (model.state is AsyncImagePainter.State.Success) 1f else 0f
            )
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier
                        .size(60.dp)
                        .alpha(transition),
                    contentScale = ContentScale.Crop,
                    painter = model,
                    contentDescription = null,
                )
                when (model.state) {
                    is AsyncImagePainter.State.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    is AsyncImagePainter.State.Error, AsyncImagePainter.State.Empty -> {
                        Text(
                            text = title.first().toString(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    else -> {

                    }
                }
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = TextStyle.Default.copy(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    ),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}