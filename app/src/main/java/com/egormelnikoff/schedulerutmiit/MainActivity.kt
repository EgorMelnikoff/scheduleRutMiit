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
import com.egormelnikoff.schedulerutmiit.core.ui.theme.ScheduleRutMiitTheme
import com.egormelnikoff.schedulerutmiit.ui.ScheduleRutMiitApp
import com.egormelnikoff.schedulerutmiit.ui.WelcomePage
import com.egormelnikoff.schedulerutmiit.ui.view_model.PreferencesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val preferencesViewModel: PreferencesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
           preferencesViewModel.appSettings.value == null
        }

        setContent {
            val appSettings by preferencesViewModel.appSettings.collectAsStateWithLifecycle()

            appSettings?.let { settings ->
                ScheduleRutMiitTheme(
                    decorPreferences = settings.decorPreferences
                ) {
                    AnimatedContent(
                        targetState = settings.skipWelcomePage,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        }
                    ) { skipWelcomePage ->
                        if (skipWelcomePage) {
                            ScheduleRutMiitApp(
                                preferencesViewModel = preferencesViewModel,
                                appSettings = settings
                            )
                        } else {
                            WelcomePage(preferencesViewModel)
                        }
                    }
                }
            }
        }
    }
}