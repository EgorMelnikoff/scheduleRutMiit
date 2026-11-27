package com.egormelnikoff.schedulerutmiit.search.ui.widget

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.egormelnikoff.schedulerutmiit.core.ui.components.chip.AppFilterChip
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.search.R
import com.egormelnikoff.schedulerutmiit.search.utils.SearchType

@Composable
internal fun SearchFilterRowWidget(
    selectedOption: SearchType,
    onSelectOption: (SearchType) -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.padding8)
    ) {
        AppFilterChip(
            title = stringResource(R.string.search_all),
            imageVector = null,
            selected = selectedOption == SearchType.ALL,
            onClick = {
                onSelectOption(SearchType.ALL)
            }
        )
        AppFilterChip(
            title = stringResource(R.string.search_groups),
            imageVector = AppTheme.icons.group,
            selected = selectedOption == SearchType.GROUPS,
            onClick = {
                onSelectOption(SearchType.GROUPS)
            }
        )
        AppFilterChip(
            title = stringResource(R.string.search_people),
            imageVector = AppTheme.icons.person,
            selected = selectedOption == SearchType.PEOPLE,
            onClick = {
                onSelectOption(SearchType.PEOPLE)
            }
        )
    }
}