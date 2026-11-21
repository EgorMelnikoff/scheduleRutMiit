package com.egormelnikoff.schedulerutmiit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.egormelnikoff.schedulerutmiit.core.navigation.AppRouter
import com.egormelnikoff.schedulerutmiit.core.settings.ui.SettingsAction
import com.egormelnikoff.schedulerutmiit.core.settings.ui.SettingsViewModel
import com.egormelnikoff.schedulerutmiit.core.ui.theme.ScheduleRutMiitTheme
import com.egormelnikoff.schedulerutmiit.ui.ScheduleRutMiitApp
import com.egormelnikoff.schedulerutmiit.ui.WelcomePage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var appRouter: AppRouter
    private val settingsViewModel: SettingsViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            settingsViewModel.state.value.appSettings == null
        }

        setContent {
            val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()

            settingsState.appSettings?.let { settings ->
                ScheduleRutMiitTheme(
                    themePreferences = settings.themePreferences
                ) {
                    AnimatedContent(
                        targetState = settings.skipWelcomePage,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        }
                    ) { skipWelcomePage ->
                        if (skipWelcomePage) {
                            ScheduleRutMiitApp(
                                settingsViewModel = settingsViewModel,
                                settingsState = settingsState,
                                appRouter = appRouter
                            )
                        } else {
                            WelcomePage {
                                settingsViewModel.onAction(SettingsAction.OnSkipWelcomePage)
                            }
                        }
                    }
                }
            }
        }
    }
}