/*
 * SPDX-FileCopyrightText: 2023-2026 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package com.dot.gallery.core.presentation.components

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.dot.gallery.R
import com.dot.gallery.core.Settings.Misc.rememberAllowBlur
import com.dot.gallery.core.Settings.Misc.rememberAutoHideNavBar
import com.dot.gallery.core.Settings.Misc.rememberOldNavbar
import com.dot.gallery.feature_node.presentation.util.LocalHazeState
import com.dot.gallery.feature_node.presentation.util.NavigationItem
import com.dot.gallery.feature_node.presentation.util.Screen
import com.dot.gallery.feature_node.presentation.util.rememberBottomBarInset
import com.dot.gallery.ui.core.icons.Albums
import dev.chrisbanes.haze.LocalHazeStyle
import dev.chrisbanes.haze.hazeEffect

@Composable
fun rememberNavigationItems(): List<NavigationItem> {
    val timelineTitle = stringResource(R.string.nav_timeline)
    val albumsTitle = stringResource(R.string.nav_albums)
    val libraryTitle = stringResource(R.string.library)
    return remember {
        mutableListOf(
            NavigationItem(
                name = timelineTitle,
                route = Screen.TimelineScreen.route,
                icon = Icons.Outlined.Photo,
            ),
            NavigationItem(
                name = albumsTitle,
                route = Screen.AlbumsScreen.route,
                icon = com.dot.gallery.ui.core.Icons.Albums,
            ),
            NavigationItem(
                name = libraryTitle,
                route = Screen.LibraryScreen(),
                icon = Icons.Outlined.PhotoLibrary
            )
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Stable
@Composable
fun AppBarContainer(
    navController: NavController,
    bottomBarState: Boolean,
    paddingValues: PaddingValues,
    isScrolling: Boolean,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val windowSizeClass = calculateWindowSizeClass(context as Activity)
    val backStackEntry by navController.currentBackStackEntryAsState()
    val bottomNavItems = rememberNavigationItems()
    val useNavRail by remember(windowSizeClass) {
        mutableStateOf(windowSizeClass.widthSizeClass > WindowWidthSizeClass.Compact)
    }
    val useOldNavbar by rememberOldNavbar()
    val hideNavBarSetting by rememberAutoHideNavBar()
    val anySelectedRoute = remember(backStackEntry) {
        bottomNavItems.any { it.route == navController.currentDestination?.route }
    }

    // The classic nav rail only applies in the old-navbar layout on wide screens.
    val showNavRail by remember(useOldNavbar, useNavRail, bottomBarState, anySelectedRoute) {
        derivedStateOf { useOldNavbar && useNavRail && bottomBarState && anySelectedRoute }
    }
    val showClassicNavbar by remember(useOldNavbar, useNavRail, bottomBarState, isScrolling, hideNavBarSetting, anySelectedRoute) {
        derivedStateOf {
            useOldNavbar && !useNavRail && bottomBarState && (!isScrolling || !hideNavBarSetting) && anySelectedRoute
        }
    }
    val showMaterialNavbar by remember(useOldNavbar, bottomBarState, isScrolling, hideNavBarSetting, anySelectedRoute) {
        derivedStateOf {
            !useOldNavbar && bottomBarState && (!isScrolling || !hideNavBarSetting) && anySelectedRoute
        }
    }
    val animatedPadding by animateDpAsState(
        targetValue = if (showNavRail) 80.dp else 0.dp,
        label = "animatedPadding"
    )

    // Render the content exactly once. Toggling "use material navigation" must only swap the
    // navigation bars below, not the whole app content. Previously content() was nested inside
    // both the useOldNavbar and !useOldNavbar AnimatedVisibility blocks, so flipping the setting
    // cross-faded (and recomposed) the entire screen, making it blink (#973).
    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.padding(start = animatedPadding)) {
            content()
        }
        // Old-navbar layout: classic rail (wide) or classic bottom bar (compact)
        AnimatedVisibility(
            visible = showNavRail,
            enter = slideInHorizontally { it * -2 },
            exit = slideOutHorizontally { it * -2 }
        ) {
            ClassicNavigationRail(
                backStackEntry = backStackEntry,
                navigationItems = bottomNavItems,
                onClick = { navigate(navController, it) }
            )
        }
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter),
            visible = showClassicNavbar,
            enter = slideInVertically { it * 2 },
            exit = slideOutVertically { it * 2 },
            content = {
                ClassicNavBar(
                    backStackEntry = backStackEntry,
                    navigationItems = bottomNavItems,
                    onClick = { navigate(navController, it) },
                )
            }
        )
        // Material (new) navbar: floating bar
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = rememberBottomBarInset(paddingValues)),
            visible = showMaterialNavbar,
            enter = slideInVertically { it * 2 },
            exit = slideOutVertically { it * 2 }
        ) {
            GooglePhotosNavigationPill(
                navController = navController,
                navItems = bottomNavItems,
                currentBackStackEntry = backStackEntry
            )
        }
    }
}

private fun navigate(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun GooglePhotosNavigationPill(
    navController: androidx.navigation.NavController,
    navItems: List<com.dot.gallery.feature_node.presentation.util.NavigationItem>,
    currentBackStackEntry: androidx.navigation.NavBackStackEntry?
) {
    val currentRoute = currentBackStackEntry?.destination?.route

    androidx.compose.foundation.layout.Row(
        modifier = androidx.compose.ui.Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        // Main Pill Box Container
        androidx.compose.foundation.layout.Row(
            modifier = androidx.compose.ui.Modifier
                .androidx.compose.ui.draw.clip(androidx.compose.foundation.shape.CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)
        ) {
            navItems.forEach { item ->
                val isSelected = currentRoute == item.route
                
                androidx.compose.foundation.layout.Row(
                    modifier = androidx.compose.ui.Modifier
                        .androidx.compose.ui.draw.clip(androidx.compose.foundation.shape.CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primaryContainer 
                            else androidx.compose.ui.graphics.Color.Transparent
                        )
                        .androidx.compose.foundation.clickable(
                            interactionSource = androidx.compose.runtime.remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                            indication = null
                        ) {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                        .padding(horizontal = if (isSelected) 18.dp else 14.dp, vertical = 10.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                ) {
                    if (isSelected) {
                        androidx.compose.material3.Icon(
                            imageVector = item.icon,
                            contentDescription = item.name,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = androidx.compose.ui.Modifier.size(18.dp)
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.width(8.dp))
                    }
                    
                    androidx.compose.material3.Text(
                        text = item.name,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer 
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium,
                        fontSize = 14.androidx.compose.ui.unit.sp
                    )
                }
            }
        }
        
        // Floating Utility Button on the right
        androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.width(8.dp))
        androidx.compose.foundation.layout.Box(
            modifier = androidx.compose.ui.Modifier
                .size(48.dp)
                .androidx.compose.ui.draw.clip(androidx.compose.foundation.shape.CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f))
                .androidx.compose.foundation.clickable {
                    navController.navigate(com.dot.gallery.feature_node.presentation.util.Screen.SearchScreen.route)
                },
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            androidx.compose.material3.Icon(
                imageVector = androidx.compose.material.icons.Icons.Rounded.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = androidx.compose.ui.Modifier.size(22.dp)
            )
        }
    }
}
