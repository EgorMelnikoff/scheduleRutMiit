package com.egormelnikoff.schedulerutmiit.ui.search

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import com.egormelnikoff.schedulerutmiit.ui.composable.Empty
import com.egormelnikoff.schedulerutmiit.ui.composable.LoadingScreen
import com.egormelnikoff.schedulerutmiit.ui.schedule.viewmodel.ScheduleUiState
import com.egormelnikoff.schedulerutmiit.ui.schedule.viewmodel.ScheduleViewModel
import com.egormelnikoff.schedulerutmiit.ui.search.viewmodel.SearchState
import com.egormelnikoff.schedulerutmiit.ui.search.viewmodel.SearchViewModel

enum class Options {
    ALL, GROUPS, PEOPLE
}

@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
    scheduleViewModel: ScheduleViewModel,
    searchState: SearchState,
    navigateToSchedule: () -> Unit,
    scheduleUiState: ScheduleUiState,
    query: String,
    onQueryChanged: (String) -> Unit,
    paddingValues: PaddingValues
) {
    var selectedOption by remember { mutableStateOf(Options.ALL) }

    Column(
        modifier = Modifier
            .padding(top = paddingValues.calculateTopPadding())
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            SearchDataField(
                onQueryChanged = onQueryChanged,
                searchViewModel = searchViewModel,
                value = query,
                selectedOptions = selectedOption
            )
            Spacer(
                modifier = Modifier.height(8.dp)
            )
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Chip(
                    title = LocalContext.current.getString(R.string.all),
                    imageVector = null,
                    selected = selectedOption == Options.ALL,
                    onSelect = {
                        selectedOption = Options.ALL
                    }
                )
                Chip(
                    title = LocalContext.current.getString(R.string.groups),
                    imageVector = ImageVector.vectorResource(R.drawable.group),
                    selected = selectedOption == Options.GROUPS,
                    onSelect = {
                        selectedOption = Options.GROUPS
                    }
                )
                Chip(
                    title = LocalContext.current.getString(R.string.people),
                    imageVector = ImageVector.vectorResource(R.drawable.person),
                    selected = selectedOption == Options.PEOPLE,
                    onSelect = {
                        selectedOption = Options.PEOPLE
                    }
                )
            }
        }
        AnimatedContent(
            modifier = Modifier.fillMaxSize(),
            targetState = searchState,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            }
        ) { stateSearch ->
            when (stateSearch) {
                is SearchState.EmptyQuery -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (scheduleUiState.savedNamedSchedules.isEmpty()) {
                            Empty(
                                imageVector = ImageVector.vectorResource(R.drawable.search),
                                subtitle = LocalContext.current.getString(R.string.enter_your_query),
                                paddingBottom = paddingValues.calculateBottomPadding()
                            )
                        } else {
                            Text(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp),
                                text = LocalContext.current.getString(R.string.maybe_you_serch),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            scheduleUiState.savedNamedSchedules.forEach { namedScheduleEntity ->
                                SearchedItem(
                                    onQueryChanged = onQueryChanged,
                                    navigateToSchedule = navigateToSchedule,
                                    searchViewModel = searchViewModel,
                                    scheduleViewModel = scheduleViewModel,
                                    type = namedScheduleEntity.type,
                                    id = namedScheduleEntity.apiId!!,
                                    title = namedScheduleEntity.shortName
                                )
                            }
                        }
                    }
                }

                is SearchState.Loading -> LoadingScreen(
                    paddingTop = 0.dp,
                    paddingBottom = paddingValues.calculateBottomPadding()
                )

                is SearchState.EmptyResult -> Empty(
                    subtitle = LocalContext.current.getString(R.string.nothing_found),
                    paddingBottom = paddingValues.calculateBottomPadding()
                )

                is SearchState.Loaded -> {
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
                        if (stateSearch.groups.isNotEmpty() && (selectedOption == Options.ALL || selectedOption == Options.GROUPS)) {
                            item {
                                Text(
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    text = LocalContext.current.getString(R.string.groups),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            items(stateSearch.groups) { group ->
                                SearchedItem(
                                    onQueryChanged = onQueryChanged,
                                    navigateToSchedule = navigateToSchedule,
                                    searchViewModel = searchViewModel,
                                    scheduleViewModel = scheduleViewModel,
                                    type = 0,
                                    id = group.id.toString(),
                                    title = group.name!!
                                )
                            }
                        }
                        if (stateSearch.people.isNotEmpty() && (selectedOption == Options.ALL || selectedOption == Options.PEOPLE)) {
                            item {
                                Text(
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    text = LocalContext.current.getString(R.string.people),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            items(stateSearch.people) { person ->
                                SearchedItem(
                                    onQueryChanged = onQueryChanged,
                                    navigateToSchedule = navigateToSchedule,
                                    scheduleViewModel = scheduleViewModel,
                                    searchViewModel = searchViewModel,
                                    type = 1,
                                    id = person.id.toString(),
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
fun SearchDataField(
    onQueryChanged: (String) -> Unit,
    value: String,
    selectedOptions: Options,
    searchViewModel: SearchViewModel
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        value = value,
        maxLines = 1,
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,

            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,

            focusedIndicatorColor = MaterialTheme.colorScheme.surface,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,

            focusedLeadingIconColor = MaterialTheme.colorScheme.onBackground,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.primary
        ),
        leadingIcon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.search_simple),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = value != "",
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
        placeholder = {
            Text(
                text = LocalContext.current.getString(
                    R.string.search
                )
            )
        },
        onValueChange = { newQuery ->
            onQueryChanged(newQuery)
        },
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(
            autoCorrectEnabled = false,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                searchViewModel.search(value.trim(), selectedOptions)
                keyboardController?.hide()
            }
        )
    )
}

@Composable
fun Chip(
    title: String,
    imageVector: ImageVector?,
    selected: Boolean,
    onSelect: (Boolean) -> Unit
) {
    FilterChip(
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = false,
            borderColor = Color.Transparent
        ),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surface,
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            labelColor = MaterialTheme.colorScheme.onSurface,
            iconColor = MaterialTheme.colorScheme.onPrimary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
        ),
        onClick = { onSelect(!selected) },
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (imageVector != null) {
                    Icon(
                        modifier = Modifier.width(16.dp),
                        imageVector = imageVector,
                        contentDescription = null
                    )
                }

                Text(title)
            }
        },
        selected = selected,
    )

}


@Composable
fun SearchedItem(
    navigateToSchedule: () -> Unit,
    onQueryChanged: (String) -> Unit,
    searchViewModel: SearchViewModel,
    scheduleViewModel: ScheduleViewModel,
    id: String,
    type: Int,
    title: String,
    subtitle: String? = null,
    imageUrl: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                onClick = {
                    navigateToSchedule()
                    scheduleViewModel.getAndSetNamedSchedule(title, id, type)
                    onQueryChanged("")
                    searchViewModel.setDefaultSearchState()
                }
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