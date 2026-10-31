package com.egormelnikoff.schedulerutmiit.search.ui.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.text_field.AppTextField
import com.egormelnikoff.schedulerutmiit.search.ui.SearchAction
import com.egormelnikoff.schedulerutmiit.search.ui.SearchState
import com.egormelnikoff.schedulerutmiit.search.utils.SearchType

@Composable
internal fun SearchHeaderWidget(
    state: SearchState,
    onAction: (SearchAction) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AppTextField(
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            value = state.query,
            placeholderText = "${stringResource(R.string.for_example)}, " +
                    when (state.searchType) {
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
                    visible = !state.isEmptyQuery || state.query.isNotEmpty(),
                    enter = scaleIn(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(500))
                ) {
                    IconButton(
                        onClick = {
                            onAction(SearchAction.OnClearParams)
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
            onAction(SearchAction.OnChangeParams(query = newValue))
        }

        SearchFilterRowWidget(
            selectedOption = state.searchType,
            onSelectOption = { searchType ->
                onAction(
                    SearchAction.OnChangeParams(
                        searchType = searchType
                    )
                )
            }
        )
    }
}