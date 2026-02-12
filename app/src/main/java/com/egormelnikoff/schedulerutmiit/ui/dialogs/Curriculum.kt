package com.egormelnikoff.schedulerutmiit.ui.dialogs

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.ui.elements.AdviceDialog
import com.egormelnikoff.schedulerutmiit.ui.elements.ClickableItem
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTextField
import com.egormelnikoff.schedulerutmiit.ui.screens.Empty
import com.egormelnikoff.schedulerutmiit.ui.screens.LoadingScreen
import com.egormelnikoff.schedulerutmiit.view_models.curriculum.CurriculumState
import com.egormelnikoff.schedulerutmiit.view_models.curriculum.CurriculumViewModel

@Composable
fun CurriculumDialog(
    searchQuery: String,
    curriculumViewModel: CurriculumViewModel,
    curriculumState: CurriculumState
) {
    var showAdviceDialog by remember { mutableStateOf(false) }

    if (showAdviceDialog) {
        AdviceDialog(
            dialogText = stringResource(R.string.search_curriculum_advice),
            painter = painterResource(R.drawable.advice),
            onDismissRequest = {
                showAdviceDialog = false
            }
        )
    }
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(
                    start = 8.dp,
                    end = 8.dp,
                    top = innerPadding.calculateTopPadding() + 16.dp,
                    bottom = innerPadding.calculateBottomPadding()
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CustomTextField(
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    value = searchQuery,
                    placeholderText = stringResource(R.string.curriculum_number),
                    leadingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.search),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    },
                    trailingIcon = {
                        AnimatedVisibility(
                            visible = searchQuery != "" || curriculumState.subjectsList.isNotEmpty(),
                            enter = scaleIn(animationSpec = tween(300)),
                            exit = fadeOut(animationSpec = tween(500))
                        ) {
                            IconButton(
                                onClick = {
                                    curriculumViewModel.setDefaultSubjectsState()
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
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Search
                    )
                ) { newValue ->
                    curriculumViewModel.changeQuery(newValue)
                }
                IconButton(
                    onClick = {
                        showAdviceDialog = true
                    }
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.info),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            AnimatedContent(
                modifier = Modifier.fillMaxSize(),
                targetState = curriculumState,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                }
            ) { curriculumState ->
                when {
                    curriculumState.isLoading -> LoadingScreen()

                    curriculumState.error != null -> {
                        Empty(
                            subtitle = curriculumState.error
                        )
                    }

                    curriculumState.isEmptyQuery -> {
                        Empty(
                            imageVector = ImageVector.vectorResource(R.drawable.search),
                            subtitle = stringResource(R.string.enter_your_query)
                        )
                    }

                    curriculumState.subjectsList.isEmpty() ->
                        Empty(
                            subtitle = stringResource(R.string.nothing_found)
                        )

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(
                                start = 8.dp,
                                end = 8.dp
                            ),
                            horizontalAlignment = Alignment.Start
                        ) {
                            items(curriculumState.subjectsList) { subject ->
                                Box(
                                    modifier = Modifier.clip(MaterialTheme.shapes.medium)
                                ) {
                                    ClickableItem(
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
                }
            }
        }
    }
}