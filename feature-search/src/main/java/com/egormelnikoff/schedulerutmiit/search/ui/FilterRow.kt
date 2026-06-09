package com.egormelnikoff.schedulerutmiit.search.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.enums.SearchType
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomFilterChip


@Composable
fun FilterRow(
    selectedOption: SearchType,
    onSelectOption: (SearchType) -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CustomFilterChip(
            title = stringResource(R.string.all),
            imageVector = null,
            selected = selectedOption == SearchType.ALL,
            onClick = {
                onSelectOption(SearchType.ALL)
            }
        )
        CustomFilterChip(
            title = stringResource(R.string.groups),
            imageVector = ImageVector.vectorResource(R.drawable.group),
            selected = selectedOption == SearchType.GROUPS,
            onClick = {
                onSelectOption(SearchType.GROUPS)
            }
        )
        CustomFilterChip(
            title = stringResource(R.string.people),
            imageVector = ImageVector.vectorResource(R.drawable.person),
            selected = selectedOption == SearchType.PEOPLE,
            onClick = {
                onSelectOption(SearchType.PEOPLE)
            }
        )
    }
}