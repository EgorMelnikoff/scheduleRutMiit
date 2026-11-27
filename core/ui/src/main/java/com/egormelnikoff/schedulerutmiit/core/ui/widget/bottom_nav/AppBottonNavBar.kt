package com.egormelnikoff.schedulerutmiit.core.ui.widget.bottom_nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egormelnikoff.schedulerutmiit.core.navigation.model.Route
import com.egormelnikoff.schedulerutmiit.core.ui.R
import com.egormelnikoff.schedulerutmiit.core.ui.components.navigation_bar.AppNavigationBar
import com.egormelnikoff.schedulerutmiit.core.ui.components.navigation_bar.AppNavigationBarItem
import com.egormelnikoff.schedulerutmiit.core.ui.components.navigation_bar.BarItem
import com.egormelnikoff.schedulerutmiit.core.ui.theme.AppTheme
import com.egormelnikoff.schedulerutmiit.core.ui.theme.isDarkTheme
import com.egormelnikoff.schedulerutmiit.core.ui.view_model.Initialize

@Composable
fun AppBottonNavBarWidget() {

    val viewModel = hiltViewModel<AppNavBarViewModel>()
    viewModel.Initialize()

    val state by viewModel.state.collectAsStateWithLifecycle()

    val scheduleIcon = AppTheme.icons.schedule
    val scheduleFillIcon = AppTheme.icons.scheduleFill
    val newsIcon = AppTheme.icons.news
    val tasksIcon = AppTheme.icons.tasks
    val tasksFillIcon = AppTheme.icons.tasksFill
    val reviewIcon = AppTheme.icons.review
    val reviewFillIcon = AppTheme.icons.reviewFill

    AppNavigationBar(
        barItems = {

            AppNavigationBarItem(
                barItem = remember {
                    BarItem(
                        title = R.string.ui_schedule,
                        icon = scheduleIcon,
                        selectedIcon = scheduleFillIcon,
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
                        icon = newsIcon,
                        selectedIcon = newsIcon,
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
                        icon = tasksIcon,
                        selectedIcon = tasksFillIcon,
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
                        icon = reviewIcon,
                        selectedIcon = reviewFillIcon,
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