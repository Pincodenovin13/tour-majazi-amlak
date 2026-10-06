package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.data.AppRepository
import com.example.model.PropertyItem
import com.example.model.UserRole
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.AgentDashboardScreen
import com.example.ui.screens.Capture360Screen
import com.example.ui.screens.CityAdsCreateScreen
import com.example.ui.screens.CityAdsViewScreen
import com.example.ui.screens.CreateTourScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MainDashboardScreen
import com.example.ui.screens.MyPropertiesScreen
import com.example.ui.screens.NotificationCenterScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.PropertyDetailTourScreen
import com.example.ui.screens.RoleSelectionScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.SubscriptionScreen
import com.example.ui.screens.UserDashboardScreen
import com.example.ui.theme.MyApplicationTheme

enum class Screen {
    SPLASH,
    MAIN_DASHBOARD,
    ROLE_SELECTION,
    LOGIN,
    AGENT_DASHBOARD,
    USER_DASHBOARD,
    CREATE_TOUR,
    CAPTURE_360,
    MY_PROPERTIES,
    CITY_ADS_VIEW,
    CITY_ADS_CREATE,
    SUBSCRIPTION,
    PROPERTY_DETAIL,
    ADMIN_PANEL,
    NOTIFICATION_CENTER,
    PROFILE
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = AppRepository.instance

        setContent {
            MyApplicationTheme {
                // Ensure RTL layout for Persian (Farsi)
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        RealEstateTourApp(repository = repository)
                    }
                }
            }
        }
    }
}

@Composable
fun RealEstateTourApp(repository: AppRepository) {
    var currentScreen by remember { mutableStateOf(Screen.SPLASH) }
    var selectedRole by remember { mutableStateOf(UserRole.AGENT) }
    var activePropertyForTour by remember { mutableStateOf<PropertyItem?>(null) }
    var selectedCityForAds by remember { mutableStateOf<String?>("ساری") }
    var screenHistory by remember { mutableStateOf(listOf(Screen.SPLASH)) }

    fun navigateTo(screen: Screen) {
        screenHistory = screenHistory + screen
        currentScreen = screen
    }

    fun navigateBack() {
        if (screenHistory.size > 1) {
            val updated = screenHistory.dropLast(1)
            screenHistory = updated
            currentScreen = updated.last()
        } else {
            currentScreen = when (selectedRole) {
                UserRole.AGENT -> Screen.AGENT_DASHBOARD
                UserRole.REGULAR_USER -> Screen.USER_DASHBOARD
            }
        }
    }

    // Android Back button handler
    BackHandler(enabled = currentScreen != Screen.SPLASH && currentScreen != Screen.MAIN_DASHBOARD) {
        if (currentScreen == Screen.AGENT_DASHBOARD || currentScreen == Screen.USER_DASHBOARD || currentScreen == Screen.ROLE_SELECTION) {
            navigateTo(Screen.MAIN_DASHBOARD)
        } else {
            navigateBack()
        }
    }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "screen_transition"
    ) { screen ->
        when (screen) {
            Screen.SPLASH -> {
                SplashScreen(
                    onSplashFinished = {
                        currentScreen = Screen.MAIN_DASHBOARD
                        screenHistory = listOf(Screen.MAIN_DASHBOARD)
                    }
                )
            }

            Screen.MAIN_DASHBOARD -> {
                MainDashboardScreen(
                    repository = repository,
                    onRolePathSelected = { role, isReferrer ->
                        selectedRole = role
                        repository.setRole(role)
                        navigateTo(Screen.LOGIN)
                    },
                    onCitySelected = { cityName ->
                        selectedCityForAds = cityName
                        navigateTo(Screen.CITY_ADS_VIEW)
                    },
                    onOpenTourViewer = { property ->
                        activePropertyForTour = property
                        navigateTo(Screen.PROPERTY_DETAIL)
                    },
                    onAdminPanelClick = {
                        navigateTo(Screen.ADMIN_PANEL)
                    }
                )
            }

            Screen.ROLE_SELECTION -> {
                RoleSelectionScreen(
                    onRoleSelected = { role ->
                        selectedRole = role
                        repository.setRole(role)
                        navigateTo(Screen.LOGIN)
                    }
                )
            }

            Screen.LOGIN -> {
                LoginScreen(
                    role = selectedRole,
                    onLoginSuccess = { phone, role ->
                        repository.login(phone, role)
                        if (role == UserRole.AGENT) {
                            currentScreen = Screen.AGENT_DASHBOARD
                            screenHistory = listOf(Screen.AGENT_DASHBOARD)
                        } else {
                            currentScreen = Screen.USER_DASHBOARD
                            screenHistory = listOf(Screen.USER_DASHBOARD)
                        }
                    },
                    onBackClick = { navigateBack() }
                )
            }

            Screen.AGENT_DASHBOARD -> {
                AgentDashboardScreen(
                    repository = repository,
                    onCreateTourClick = { navigateTo(Screen.CREATE_TOUR) },
                    onMyPropertiesClick = { navigateTo(Screen.MY_PROPERTIES) },
                    onSubscriptionClick = { navigateTo(Screen.SUBSCRIPTION) },
                    onCityAdsClick = { navigateTo(Screen.CITY_ADS_VIEW) },
                    onAnalyticsClick = { navigateTo(Screen.ADMIN_PANEL) },
                    onNotificationsClick = { navigateTo(Screen.NOTIFICATION_CENTER) },
                    onCapture360Click = { navigateTo(Screen.CAPTURE_360) },
                    onProfileClick = { navigateTo(Screen.PROFILE) },
                    onOpenTourViewer = { property ->
                        activePropertyForTour = property
                        navigateTo(Screen.PROPERTY_DETAIL)
                    }
                )
            }

            Screen.USER_DASHBOARD -> {
                UserDashboardScreen(
                    repository = repository,
                    onOpenTourViewer = { property ->
                        activePropertyForTour = property
                        navigateTo(Screen.PROPERTY_DETAIL)
                    },
                    onCityAdsClick = { navigateTo(Screen.CITY_ADS_VIEW) },
                    onProfileClick = { navigateTo(Screen.PROFILE) }
                )
            }

            Screen.CREATE_TOUR -> {
                CreateTourScreen(
                    repository = repository,
                    onTourCreated = {
                        navigateTo(Screen.MY_PROPERTIES)
                    },
                    onLaunchCamera360 = {
                        navigateTo(Screen.CAPTURE_360)
                    },
                    onBackClick = { navigateBack() }
                )
            }

            Screen.CAPTURE_360 -> {
                Capture360Screen(
                    repository = repository,
                    onCaptureFinished = { _ ->
                        if (!screenHistory.contains(Screen.CREATE_TOUR)) {
                            navigateTo(Screen.CREATE_TOUR)
                        } else {
                            navigateBack()
                        }
                    },
                    onBackClick = { navigateBack() }
                )
            }

            Screen.MY_PROPERTIES -> {
                MyPropertiesScreen(
                    repository = repository,
                    onOpenTourViewer = { property ->
                        activePropertyForTour = property
                        navigateTo(Screen.PROPERTY_DETAIL)
                    },
                    onCreateTourClick = { navigateTo(Screen.CREATE_TOUR) },
                    onBackClick = { navigateBack() }
                )
            }

            Screen.CITY_ADS_VIEW -> {
                CityAdsViewScreen(
                    repository = repository,
                    initialCity = selectedCityForAds,
                    onOpenTour = { property ->
                        activePropertyForTour = property
                        navigateTo(Screen.PROPERTY_DETAIL)
                    },
                    onCreateAdClick = {
                        navigateTo(Screen.CITY_ADS_CREATE)
                    },
                    onBackClick = { navigateBack() }
                )
            }

            Screen.CITY_ADS_CREATE -> {
                CityAdsCreateScreen(
                    repository = repository,
                    onAdCreated = {
                        navigateTo(Screen.CITY_ADS_VIEW)
                    },
                    onBackClick = { navigateBack() }
                )
            }

            Screen.SUBSCRIPTION -> {
                SubscriptionScreen(
                    repository = repository,
                    onBackClick = { navigateBack() }
                )
            }

            Screen.PROPERTY_DETAIL -> {
                val prop = activePropertyForTour ?: repository.properties.collectAsState().value.first()
                PropertyDetailTourScreen(
                    property = prop,
                    repository = repository,
                    onBackClick = { navigateBack() }
                )
            }

            Screen.ADMIN_PANEL -> {
                AdminPanelScreen(
                    repository = repository,
                    onBackClick = { navigateBack() }
                )
            }

            Screen.NOTIFICATION_CENTER -> {
                NotificationCenterScreen(
                    repository = repository,
                    onBackClick = { navigateBack() }
                )
            }

            Screen.PROFILE -> {
                ProfileScreen(
                    repository = repository,
                    onSubscriptionClick = { navigateTo(Screen.SUBSCRIPTION) },
                    onNotificationClick = { navigateTo(Screen.NOTIFICATION_CENTER) },
                    onAdminPanelClick = { navigateTo(Screen.ADMIN_PANEL) },
                    onSwitchRoleClick = { navigateTo(Screen.MAIN_DASHBOARD) },
                    onLogoutClick = {
                        currentScreen = Screen.MAIN_DASHBOARD
                        screenHistory = listOf(Screen.MAIN_DASHBOARD)
                    },
                    onBackClick = { navigateBack() }
                )
            }
        }
    }
}
