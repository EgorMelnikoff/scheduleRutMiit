package com.egormelnikoff.schedulerutmiit.core.ui.widget.bottom_nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egormelnikoff.schedulerutmiit.core.common.R as CommonR
import com.egormelnikoff.schedulerutmiit.core.navigation.model.Route
import com.egormelnikoff.schedulerutmiit.core.ui.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.navigation_bar.AppNavigationBar
import com.egormelnikoff.schedulerutmiit.core.ui.components.navigation_bar.AppNavigationBarItem
import com.egormelnikoff.schedulerutmiit.core.ui.components.navigation_bar.BarItem
import com.egormelnikoff.schedulerutmiit.core.ui.theme.isDarkTheme
import com.egormelnikoff.schedulerutmiit.core.ui.view_model.Initialize

@Composable
fun AppBottonNavBarWidget() {

    val viewModel = hiltViewModel<AppNavBarViewModel>()
    viewModel.Initialize()

    val state by viewModel.state.collectAsStateWithLifecycle()

    AppNavigationBar(
        barItems = {

            AppNavigationBarItem(
                barItem = remember {
                    BarItem(
                        title = R.string.ui_schedule,
                        iconRes = CommonR.drawable.schedule,
                        selectedIconRes = CommonR.drawable.schedule_fill,
                    )
                },
                isSelected = state.currentPage is Route.Page.Schedule,
                onClick = {
                    viewModel.onAction(
                        AppNavBarAction.OnNavigatePage(Route.Page.Schedule)
                    )
                }
            )


            AppNavigationBarItem(
                barItem = remember {
                    BarItem(
                        title = R.string.ui_news,
                        iconRes = CommonR.drawable.news,
                        selectedIconRes = CommonR.drawable.news,
                    )
                },
                showBadge = state.updatesAvailable,
                isSelected = state.currentPage is Route.Page.News,
            ) {
                viewModel.onAction(
                    AppNavBarAction.OnNavigatePage(Route.Page.News)
                )
            }

            AppNavigationBarItem(
                barItem = remember {
                    BarItem(
                        title = R.string.ui_tasks,
                        iconRes = CommonR.drawable.tasks,
                        selectedIconRes = CommonR.drawable.tasks_fill,
                    )
                },
                isSelected = state.currentPage is Route.Page.Tasks,
            ) {
                viewModel.onAction(
                    AppNavBarAction.OnNavigatePage(Route.Page.Tasks)
                )
            }

            AppNavigationBarItem(
                barItem = remember {
                    BarItem(
                        title = R.string.ui_review,
                        iconRes = CommonR.drawable.review,
                        selectedIconRes = CommonR.drawable.review_fill,
                    )
                },
                isSelected = state.currentPage is Route.Page.Review
            ) {
                viewModel.onAction(
                    AppNavBarAction.OnNavigatePage(Route.Page.Review)
                )
            }

        },
        isDarkTheme = state.theme.isDarkTheme()
    )
}