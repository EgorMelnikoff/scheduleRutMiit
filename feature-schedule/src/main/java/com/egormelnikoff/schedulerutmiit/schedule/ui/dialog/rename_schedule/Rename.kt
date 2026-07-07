package com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.rename_schedule

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomButton
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomTextField
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomTopAppBar
import com.egormelnikoff.schedulerutmiit.core.ui.elements.composable.LoadingScreen
import com.egormelnikoff.schedulerutmiit.core.ui.navigation.Route
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.rename_schedule.view_model.RenameViewModel
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.rename_schedule.view_model.state.RenameState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenameDialog(
    renameDialog: Route.Dialog.RenameNamedScheduleDialog,
    onBack: () -> Unit
) {
    val renameViewModel = hiltViewModel<RenameViewModel, RenameViewModel.Factory> { factory ->
        factory.create(renameDialog.namedScheduleId)
    }
    when (val renameState = renameViewModel.renameState.collectAsStateWithLifecycle().value) {
        is RenameState.Loading -> LoadingScreen()

        is RenameState.Loaded -> {
            Scaffold(
                topBar = {
                    CustomTopAppBar(
                        titleText = stringResource(R.string.renaming),
                        navAction = onBack
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = innerPadding.calculateTopPadding() + 12.dp,
                            bottom = innerPadding.calculateBottomPadding(),
                            start = 16.dp, end = 16.dp
                        )
                        .background(MaterialTheme.colorScheme.background)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CustomTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = renameState.newName,
                        maxSymbols = 50,
                        keyboardOptions = KeyboardOptions(
                            autoCorrectEnabled = false,
                            imeAction = ImeAction.Default
                        ),
                        placeholderText = stringResource(R.string.name),
                        trailingIcon = {
                            AnimatedVisibility(
                                visible = renameState.newName.isNotEmpty(),
                                enter = scaleIn(animationSpec = tween(300)),
                                exit = fadeOut(animationSpec = tween(500))
                            ) {
                                IconButton(
                                    onClick = {
                                        renameViewModel.updateName("")
                                    }
                                ) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.clear),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    ) { newValue ->
                        renameViewModel.updateName(newValue)
                    }
                    CustomButton(
                        modifier = Modifier.fillMaxWidth(),
                        buttonTitle = stringResource(R.string.save),
                        enabled = renameState.renameEnabled,
                        onClick = {
                            renameViewModel.renameNamedSchedule()
                            onBack()
                        }
                    )
                }
            }
        }
    }
}