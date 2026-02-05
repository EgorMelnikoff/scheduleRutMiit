package com.egormelnikoff.schedulerutmiit.ui.dialogs

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomButton
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTextField
import com.egormelnikoff.schedulerutmiit.ui.elements.CustomTopAppBar
import com.egormelnikoff.schedulerutmiit.ui.navigation.NavigationActions
import com.egormelnikoff.schedulerutmiit.ui.state.actions.ScheduleActions

@Composable
fun RenameDialog(
    namedScheduleEntity: NamedScheduleEntity,
    navigationActions: NavigationActions,
    scheduleActions: ScheduleActions
) {
    var newName by remember { mutableStateOf(namedScheduleEntity.fullName) }
    Scaffold(
        topBar = {
            CustomTopAppBar(
                titleText = stringResource(R.string.renaming),
                navAction = {
                    navigationActions.onBack()
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding(),
                    start = 16.dp, end = 16.dp
                )
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CustomTextField(
                modifier = Modifier.fillMaxWidth(),
                value = newName,
                maxSymbols = 50,
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = false,
                    imeAction = ImeAction.Default
                ),
                placeholderText = stringResource(R.string.name),
                trailingIcon = {
                    AnimatedVisibility(
                        visible = newName != "",
                        enter = scaleIn(animationSpec = tween(300)),
                        exit = fadeOut(animationSpec = tween(500))
                    ) {
                        IconButton(
                            onClick = {
                                newName = ""
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
                newName = newValue
            }
            CustomButton(
                modifier = Modifier.fillMaxWidth(),
                buttonTitle = stringResource(R.string.save),
                enabled = newName != "",
                onClick = {
                    scheduleActions.onConfirmRenameNamedSchedule(
                        namedScheduleEntity,
                        newName.trim()
                    )
                    navigationActions.onBack()
                }
            )
        }
    }
}