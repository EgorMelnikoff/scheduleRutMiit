package com.egormelnikoff.schedulerutmiit.feature_curriculum.ui.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.text_field.AppTextField
import com.egormelnikoff.schedulerutmiit.feature_curriculum.ui.CurriculumAction
import com.egormelnikoff.schedulerutmiit.feature_curriculum.ui.CurriculumState

@Composable
internal fun CurriculumSearchInputWidget(
    state: CurriculumState,
    onAction: (CurriculumAction) -> Unit
) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AppTextField(
            modifier = Modifier.weight(1f),
            maxLines = 1,
            value = state.searchQuery,
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
                    visible = state.searchQuery != "" || state.subjectsList.isNotEmpty(),
                    enter = scaleIn(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(500))
                ) {
                    IconButton(
                        onClick = {
                            onAction(CurriculumAction.OnClearState)
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
            onAction(CurriculumAction.OnChangeQuery(newValue))
        }

        IconButton(
            onClick = {
                onAction(CurriculumAction.OnShowAdviceDialog)
            }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.info),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}