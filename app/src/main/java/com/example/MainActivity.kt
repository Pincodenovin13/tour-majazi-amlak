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
import com.example.model.AdItem
import com.example.model.PropertyItem
import com.example.model.UserRole
import com.example.ui.screens.AgentDashboardScreen
import com.example.ui.screens.AgentPublicProfileScreen
import com.example.ui.screens.AgentReferralsScreen
import com.example.ui.screens.Capture360Screen
import com.example.ui.screens.CityAdsCreateScreen
import com.example.ui.screens.CityAdsViewScreen
import com.example.ui.screens.CreateTourScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MainDashboardScreen
import com.example.ui.screens.MyPropertiesScreen
import com.example.ui.screens.NotificationCenterScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.PropertyDetailTourScreen
import com.example.ui.screens.RoleSelectionScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.SubscriptionScreen
import com.example.ui.screens.UserDashboardScreen
import com.example.ui.theme.MyApplicationTheme

enum class Screen {
    SPLASH,
    ONBOARDING,
    MAIN_DASHBOARD,
    ROLE_SELECTION,
    LOGIN,
    AGENT_DASHBOARD,
    AGENT_REFERRALS,
    USER_DASHBOARD,
    CREATE_TOUR,
    CAPTURE_360,
    MY_PROPERTIES,
    CITY_ADS_VIEW,
    CITY_ADS_CREATE,
    AGENT_PUBLIC_PROFILE,
    SUBSCRIPTION,
    PROPERTY_DETAIL,
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
    var activeAdForProfile by remember { mutableStateOf<AdItem?>(null) }
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
                UserRole.PUBLIC_VISITOR -> Screen.MAIN_DASHBOARD
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
            // Issue 2 & 8: Splash (3s) -> 5 Onboarding Slides (swipeable) -> User Type Selection
            Screen.SPLASH -> {
                SplashScreen(
                    onSplashFinished = {
                        currentScreen = Screen.ONBOARDING
                        screenHistory = listOf(Screen.ONBOARDING)
                    }
                )
            }

            Screen.ONBOARDING -> {
                OnboardingScreen(
                    onFinish = {
                        currentScreen = Screen.ROLE_SELECTION
                        screenHistory = listOf(Screen.ROLE_SELECTION)
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
                    }
                )
            }

            // Issue 9: Updated user type selection naming & routing
            Screen.ROLE_SELECTION -> {
                RoleSelectionScreen(
                    onRoleSelected = { role ->
                        if (role == UserRole.PUBLIC_VISITOR) {
                            currentScreen = Screen.MAIN_DASHBOARD
                            screenHistory = listOf(Screen.MAIN_DASHBOARD)
                        } else {
                            selectedRole = role
                            repository.setRole(role)
                            navigateTo(Screen.LOGIN)
                        }
                    }
                )
            }

            // Issue 7: Full Registration and OTP Login
            Screen.LOGIN -> {
                LoginScreen(
                    role = selectedRole,
                    repository = repository,
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
                    onAnalyticsClick = { navigateTo(Screen.CITY_ADS_VIEW) },
                    onNotificationsClick = { navigateTo(Screen.NOTIFICATION_CENTER) },
                    onMyReferralsClick = { navigateTo(Screen.AGENT_REFERRALS) },
                    onCapture360Click = { navigateTo(Screen.CAPTURE_360) },
                    onProfileClick = { navigateTo(Screen.PROFILE) },
                    onOpenTourViewer = { property ->
                        activePropertyForTour = property
                        navigateTo(Screen.PROPERTY_DETAIL)
                    }
                )
            }

            Screen.AGENT_REFERRALS -> {
                AgentReferralsScreen(
                    repository = repository,
                    onBackClick = { navigateBack() }
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

            // Issue 3 & Issue 6: City Ads View with Agent Profile Redirect & Create Ad
            Screen.CITY_ADS_VIEW -> {
                CityAdsViewScreen(
                    repository = repository,
                    initialCity = selectedCityForAds,
                    onOpenTour = { property ->
                        activePropertyForTour = property
                        navigateTo(Screen.PROPERTY_DETAIL)
                    },
                    onOpenAgentProfile = { ad ->
                        activeAdForProfile = ad
                        navigateTo(Screen.AGENT_PUBLIC_PROFILE)
                    },
                    onCreateAdClick = {
                        navigateTo(Screen.CITY_ADS_CREATE)
                    },
                    onBackClick = { navigateBack() }
                )
            }

            // Issue 1, 2, 4, 10: Create City Ad with Real-Time Pricing, Gallery Upload, Clean Payment
            Screen.CITY_ADS_CREATE -> {
                CityAdsCreateScreen(
                    repository = repository,
                    onAdCreated = {
                        navigateTo(Screen.CITY_ADS_VIEW)
                    },
                    onBackClick = { navigateBack() }
                )
            }

            // Issue 3: Agent Public Profile with Active 360 Tours & Views Count
            Screen.AGENT_PUBLIC_PROFILE -> {
                val ad = activeAdForProfile
                AgentPublicProfileScreen(
                    agentName = ad?.agentName ?: "مهندس کیان آریا",
                    city = ad?.city ?: selectedCityForAds ?: "ساری",
                    bannerViewCount = ad?.viewCount ?: 0,
                    repository = repository,
                    onOpenTour = { property ->
                        activePropertyForTour = property
                        navigateTo(Screen.PROPERTY_DETAIL)
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
                    onSwitchRoleClick = { navigateTo(Screen.ROLE_SELECTION) },
                    onLogoutClick = {
                        currentScreen = Screen.ROLE_SELECTION
                        screenHistory = listOf(Screen.ROLE_SELECTION)
                    },
                    onBackClick = { navigateBack() }
                )
            }
        }
    }
}
