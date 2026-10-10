package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserRole
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard

enum class NavTab {
    HOME,
    SEARCH,
    FAVORITES,
    MESSAGES,
    PROFILE_OR_PANEL
}

/**
 * Consistent Unified Top Header across the app:
 * - Left (RTL: right): Back button OR Logo/Menu
 * - Center: Title
 * - Right (RTL: left): Notification bell (if logged in) + profile avatar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHeader(
    title: String,
    onBackClick: (() -> Unit)? = null,
    showNotifications: Boolean = false,
    unreadNotificationCount: Int = 0,
    onNotificationClick: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null,
    onSearchClick: (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "بازگشت",
                        tint = Color.White
                    )
                }
            }
        },
        actions = {
            if (actions != null) {
                actions()
            } else {
                if (onSearchClick != null) {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "جستجو",
                            tint = Color.White
                        )
                    }
                }
                if (showNotifications && onNotificationClick != null) {
                    IconButton(onClick = onNotificationClick) {
                        if (unreadNotificationCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = AccentOrange,
                                        contentColor = Color.White
                                    ) {
                                        Text(unreadNotificationCount.toString(), fontSize = 10.sp)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "اعلان‌ها",
                                    tint = AccentYellow
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "اعلان‌ها",
                                tint = Color.White
                            )
                        }
                    }
                }
                if (onProfileClick != null) {
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "پروفایل",
                            tint = AccentYellow
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkSurface,
            titleContentColor = Color.White
        ),
        modifier = modifier
    )
}

/**
 * 5-Tab Consistent Bottom Navigation Bar:
 * - خانه (Home)
 * - جستجو (Search)
 * - علاقه‌مندی‌ها (Favorites)
 * - پیام‌ها (Messages)
 * - پنل من (برای مشاورین) / پروفایل (برای کاربران عادی و میهمان)
 */
@Composable
fun AppBottomNav(
    selectedTab: NavTab,
    userRole: UserRole,
    isLoggedIn: Boolean,
    unreadMessagesCount: Int = 0,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = DarkSurface,
        tonalElevation = 8.dp,
        modifier = modifier.testTag("app_bottom_nav_bar")
    ) {
        // 1. Home
        NavigationBarItem(
            selected = selectedTab == NavTab.HOME,
            onClick = { onTabSelected(NavTab.HOME) },
            icon = {
                Icon(Icons.Default.Home, contentDescription = "خانه")
            },
            label = {
                Text(
                    text = "خانه",
                    fontWeight = if (selectedTab == NavTab.HOME) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 11.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = AccentYellow,
                indicatorColor = AccentYellow,
                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                unselectedTextColor = Color.White.copy(alpha = 0.6f)
            )
        )

        // 2. Search
        NavigationBarItem(
            selected = selectedTab == NavTab.SEARCH,
            onClick = { onTabSelected(NavTab.SEARCH) },
            icon = {
                Icon(Icons.Default.Search, contentDescription = "جستجو")
            },
            label = {
                Text(
                    text = "جستجو",
                    fontWeight = if (selectedTab == NavTab.SEARCH) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 11.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = AccentYellow,
                indicatorColor = AccentYellow,
                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                unselectedTextColor = Color.White.copy(alpha = 0.6f)
            )
        )

        // 3. Favorites
        NavigationBarItem(
            selected = selectedTab == NavTab.FAVORITES,
            onClick = { onTabSelected(NavTab.FAVORITES) },
            icon = {
                Icon(
                    if (selectedTab == NavTab.FAVORITES) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "علاقه‌مندی‌ها"
                )
            },
            label = {
                Text(
                    text = "نشان‌شده‌ها",
                    fontWeight = if (selectedTab == NavTab.FAVORITES) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 11.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = AccentYellow,
                indicatorColor = AccentYellow,
                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                unselectedTextColor = Color.White.copy(alpha = 0.6f)
            )
        )

        // 4. Messages
        NavigationBarItem(
            selected = selectedTab == NavTab.MESSAGES,
            onClick = { onTabSelected(NavTab.MESSAGES) },
            icon = {
                if (unreadMessagesCount > 0) {
                    BadgedBox(
                        badge = {
                            Badge(containerColor = AccentOrange, contentColor = Color.White) {
                                Text(unreadMessagesCount.toString(), fontSize = 10.sp)
                            }
                        }
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = "پیام‌ها")
                    }
                } else {
                    Icon(Icons.Default.Chat, contentDescription = "پیام‌ها")
                }
            },
            label = {
                Text(
                    text = "پیام‌ها",
                    fontWeight = if (selectedTab == NavTab.MESSAGES) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 11.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = AccentYellow,
                indicatorColor = AccentYellow,
                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                unselectedTextColor = Color.White.copy(alpha = 0.6f)
            )
        )

        // 5. Profile or Panel
        val profileTitle = when {
            userRole == UserRole.AGENT && isLoggedIn -> "پنل من"
            userRole == UserRole.REGULAR_USER && isLoggedIn -> "پورسانت من"
            else -> "پروفایل"
        }
        val profileIcon: ImageVector = when {
            userRole == UserRole.AGENT && isLoggedIn -> Icons.Default.Dashboard
            else -> Icons.Default.Person
        }

        NavigationBarItem(
            selected = selectedTab == NavTab.PROFILE_OR_PANEL,
            onClick = { onTabSelected(NavTab.PROFILE_OR_PANEL) },
            icon = {
                Icon(profileIcon, contentDescription = profileTitle)
            },
            label = {
                Text(
                    text = profileTitle,
                    fontWeight = if (selectedTab == NavTab.PROFILE_OR_PANEL) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 11.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = AccentYellow,
                indicatorColor = AccentYellow,
                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                unselectedTextColor = Color.White.copy(alpha = 0.6f)
            )
        )
    }
}
