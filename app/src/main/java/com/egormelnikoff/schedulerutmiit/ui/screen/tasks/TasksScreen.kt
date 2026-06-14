package com.egormelnikoff.schedulerutmiit.ui.screen.tasks

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.ui.elements.CustomTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    externalPadding: PaddingValues
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                titleText = stringResource(R.string.tasks)
            ) {
                IconButton(
                    onClick = {

                    }
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.add),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    ) { internalPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp,
                top = internalPadding.calculateTopPadding(),
                bottom = externalPadding.calculateBottomPadding()
            )
        ) {
            items(100) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Tasks"
                )
            }
        }
    }
}