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
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.rounded.Search
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
import androidx.compose.ui.unit.sp
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

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.padding(start = animatedPadding)) {
            content()
        }
        
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
        
        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = rememberBottomBarInset(paddingValues)),
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
fun GalleryNavBar(
    modifier: Modifier,
    backStackEntry: NavBackStackEntry?,
    navigationItems: List<NavigationItem>,
    onClick: (route: String) -> Unit,
) {
    val allowBlur by rememberAllowBlur()
    val surfaceColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
    val backgroundModifier = remember (allowBlur) {
        if (!allowBlur) {
            Modifier.background(
                color = surfaceColor,
                shape = RoundedCornerShape(100)
            )
        } else {
            Modifier
        }
    }
    Row(
        modifier = Modifier
            .padding(horizontal = 32.dp, vertical = 32.dp)
            .then(modifier)
            .height(64.dp)
            .clip(RoundedCornerShape(100))
            .then(backgroundModifier)
            .hazeEffect(
                state = LocalHazeState.current,
                style = LocalHazeStyle.current
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        navigationItems.forEach { item ->
            val selected = remember(item, backStackEntry) {
                item.route == backStackEntry?.destination?.route
            }
            GalleryNavBarItem(
                navItem = item,
                isSelected = selected,
                onClick = onClick
            )
        }
    }
}

@Stable
@Composable
private fun Label(item: NavigationItem) = Text(
    text = item.name,
    fontWeight = FontWeight.Medium,
    style = MaterialTheme.typography.bodyMedium,
)

@Stable
@Composable
private fun Icon(item: NavigationItem) = Icon(
    imageVector = item.icon,
    contentDescription = item.name,
)

@Composable
fun ClassicNavBar(
    backStackEntry: NavBackStackEntry?,
    navigationItems: List<NavigationItem>,
    onClick: (route: String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
    ) {
        navigationItems.forEach { item ->
            val selected = item.route == backStackEntry?.destination?.route
            NavigationBarItem(
                selected = selected,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                onClick = {
                    if (!selected) {
                        onClick(item.route)
                    }
                },
                label = { Label(item) },
                icon = { Icon(item) }
            )
        }
    }
}

@Composable
private fun ClassicNavigationRail(
    backStackEntry: NavBackStackEntry?,
    navigationItems: List<NavigationItem>,
    onClick: (route: String) -> Unit
) {
    NavigationRail(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Spacer(Modifier.weight(1f))
        navigationItems.forEach { item ->
            val selected = item.route == backStackEntry?.destination?.route
            NavigationRailItem(
                selected = selected,
                colors = NavigationRailItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
                onClick = {
                    if (!selected) {
                        onClick(item.route)
                    }
                },
                label = { Label(item) },
                icon = { Icon(item) }
            )
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
fun RowScope.GalleryNavBarItem(
    navItem: NavigationItem,
    isSelected: Boolean,
    onClick: (route: String) -> Unit,
) {
    val mutableInteraction = remember { MutableInteractionSource() }
    val selectedColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
        label = "selectedColor"
    )
    val selectedIconColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "selectedIconColor"
    )
    Box(
        modifier = Modifier
            .height(64.dp)
            .weight(1f)
            .clickable(
                indication = null,
                interactionSource = mutableInteraction,
                onClick = {}
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .height(32.dp)
                .width(64.dp)
                .background(
                    color = selectedColor,
                    shape = RoundedCornerShape(percent = 100)
                )
                .clip(RoundedCornerShape(100))
                .clickable { if (!isSelected) onClick(navItem.route) },
        )
        Icon(
            modifier = Modifier
                .size(22.dp),
            imageVector = navItem.icon,
            contentDescription = navItem.name,
            tint = selectedIconColor
        )
    }
}

@Composable
fun GooglePhotosNavigationPill(
    navController: NavController,
    navItems: List<NavigationItem>,
    currentBackStackEntry: NavBackStackEntry?
) {
    val currentRoute = currentBackStackEntry?.destination?.route
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Main Pill Box Container
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            navItems.forEach { item ->
                val isSelected = currentRoute == item.route
                
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primaryContainer 
                            else Color.Transparent
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.name,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    
                    Text(
                        text = item.name,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer 
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
        }
        
        // Floating Utility Button on the right
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f))
                .clickable {
                    navController.navigate(Screen.SearchScreen.route)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
