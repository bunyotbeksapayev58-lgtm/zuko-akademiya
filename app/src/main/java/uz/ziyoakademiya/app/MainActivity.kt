package uz.ziyoakademiya.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import uz.ziyoakademiya.app.core.theme.ZiyoAkademiyaTheme
import uz.ziyoakademiya.app.ui.navigation.AppNavigation
import uz.ziyoakademiya.app.ui.navigation.Screen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as ZiyoApplication

        setContent {
            val userPrefs by app.userPreferencesRepository.userPreferencesFlow.collectAsState(
                initial = null
            )

            val isSystemDark = isSystemInDarkTheme()
            val useDarkTheme = when {
                userPrefs?.useSystemTheme == true -> isSystemDark
                userPrefs?.isDarkMode == true -> true
                else -> false
            }

            ZiyoAkademiyaTheme(darkTheme = useDarkTheme) {
                userPrefs?.let { prefs ->
                    val navController = rememberNavController()
                    val startDestination = if (prefs.isOnboardingCompleted) {
                        Screen.Home.route
                    } else {
                        Screen.Onboarding.route
                    }

                    AppNavigation(
                        app = app,
                        navController = navController,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}
