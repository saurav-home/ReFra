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
    [span_2](start_span)val timelineTitle = stringResource(R.string.nav_timeline)[span_2](end_span)
    [span_3](start_span)val albumsTitle = stringResource(R.string.nav_albums)[span_3](end_span)
    [span_4](start_span)val libraryTitle = stringResource(R.string.library)[span_4](end_span)
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
            [span_5](start_span)),[span_5](end_span)
            NavigationItem(
                name = libraryTitle,
                [span_6](start_span)route = Screen.LibraryScreen(),[span_6](end_span)
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
    [span_7](start_span)val context = LocalContext.current[span_7](end_span)
    [span_8](start_span)val windowSizeClass = calculateWindowSizeClass(context as Activity)[span_8](end_span)
    [span_9](start_span)val backStackEntry by navController.currentBackStackEntryAsState()[span_9](end_span)
    [span_10](start_span)val bottomNavItems = rememberNavigationItems()[span_10](end_span)
    val useNavRail by remember(windowSizeClass) {
        mutableStateOf(windowSizeClass.widthSizeClass > WindowWidthSizeClass.Compact)
    [span_11](start_span)}
    val useOldNavbar by rememberOldNavbar()[span_11](end_span)
    [span_12](start_span)val hideNavBarSetting by rememberAutoHideNavBar()[span_12](end_span)
    val anySelectedRoute = remember(backStackEntry) {
        bottomNavItems.any { it.route == navController.currentDestination?.route }
    [span_13](start_span)}

    val showNavRail by remember(useOldNavbar, useNavRail, bottomBarState, anySelectedRoute) {
        derivedStateOf { useOldNavbar && useNavRail && bottomBarState && anySelectedRoute }
    }[span_13](end_span)
    val showClassicNavbar by remember(useOldNavbar, useNavRail, bottomBarState, isScrolling, hideNavBarSetting, anySelectedRoute) {
        derivedStateOf {
            useOldNavbar && !useNavRail && bottomBarState && (!isScrolling || !hideNavBarSetting) && anySelectedRoute
        }
    [span_14](start_span)}
    val showMaterialNavbar by remember(useOldNavbar, bottomBarState, isScrolling, hideNavBarSetting, anySelectedRoute) {
        derivedStateOf {
            !useOldNavbar && bottomBarState && (!isScrolling || !hideNavBarSetting) && anySelectedRoute
        }
    }[span_14](end_span)
    val animatedPadding by animateDpAsState(
        targetValue = if (showNavRail) 80.dp else 0.dp,
        label = "animatedPadding"
    [span_15](start_span))

    Box(modifier = Modifier.fillMaxSize()) {[span_15](end_span)
        Box(modifier = Modifier.padding(start = animatedPadding)) {
            content()
        [span_16](start_span)}
        
        AnimatedVisibility(
            visible = showNavRail,
            enter = slideInHorizontally { it * -2 },
            exit = slideOutHorizontally { it * -2 }[span_16](end_span)
        ) {
            ClassicNavigationRail(
                backStackEntry = backStackEntry,
                navigationItems = bottomNavItems,
                onClick = { navigate(navController, it) }
            [span_17](start_span))
        }
        
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter),
            visible = showClassicNavbar,
            enter = slideInVertically { it * 2 },
            exit = slideOutVertically { it * 2 },
            content = {[span_17](end_span)
                ClassicNavBar(
                    backStackEntry = backStackEntry,
                    navigationItems = bottomNavItems,
                    onClick = { navigate(navController, it) },
                [span_18](start_span))
            }
        )
        
        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomEnd)[span_18](end_span)
                .padding(bottom = rememberBottomBarInset(paddingValues)),
            visible = showMaterialNavbar,
            enter = slideInVertically { it * 2 },
            exit = slideOutVertically { it * 2 }
        ) {
            GooglePhotosNavigationPill(
                [span_19](start_span)navController = navController,[span_19](end_span)
                navItems = bottomNavItems,
                currentBackStackEntry = backStackEntry
            )
        }   
    }
}

private fun navigate(navController: NavController, route: String) {
    [span_20](start_span)navController.navigate(route) {[span_20](end_span)
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        [span_21](start_span)}
        launchSingleTop = true[span_21](end_span)
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
    [span_22](start_span)val allowBlur by rememberAllowBlur()[span_22](end_span)
    [span_23](start_span)val surfaceColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)[span_23](end_span)
    [span_24](start_span)val backgroundModifier = remember (allowBlur) {[span_24](end_span)
        if (!allowBlur) {
            Modifier.background(
                color = surfaceColor,
                shape = RoundedCornerShape(100)
            )
        } else {
            Modifier
        [span_25](start_span)}
    }
    Row(
        modifier = Modifier
            .padding(horizontal = 32.dp, vertical = 32.dp)
            .then(modifier)
            .height(64.dp)
            .clip(RoundedCornerShape(100))
            .then(backgroundModifier)
            .hazeEffect(
                state = LocalHazeState.current,[span_25](end_span)
                style = LocalHazeStyle.current
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        navigationItems.forEach { item ->
            val selected = remember(item, backStackEntry) {
                [span_26](start_span)item.route == backStackEntry?.destination?.route[span_26](end_span)
            }
            GalleryNavBarItem(
                navItem = item,
                isSelected = selected,
                onClick = onClick
            )
        [span_27](start_span)}
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
            val selected = item.route == backStackEntry?.destination?.route[span_27](end_span)
            NavigationBarItem(
                selected = selected,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    [span_28](start_span)selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,[span_28](end_span)
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                [span_29](start_span)onClick = {[span_29](end_span)
                    if (!selected) {
                        onClick(item.route)
                    }
                },
                [span_30](start_span)label = { Label(item) },[span_30](end_span)
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
        [span_31](start_span)Spacer(Modifier.weight(1f))[span_31](end_span)
        [span_32](start_span)navigationItems.forEach { item ->[span_32](end_span)
            val selected = item.route == backStackEntry?.destination?.route
            NavigationRailItem(
                selected = selected,
                colors = NavigationRailItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    [span_33](start_span)selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,[span_33](end_span)
                ),
                onClick = {
                    if (!selected) {
                        onClick(item.route)
                    [span_34](start_span)}
                },
                label = { Label(item) },
                icon = { Icon(item) }
            )
        }
        Spacer(Modifier.weight(1f))[span_34](end_span)
    }
}

@Composable
fun RowScope.GalleryNavBarItem(
    navItem: NavigationItem,
    isSelected: Boolean,
    onClick: (route: String) -> Unit,
) {
    [span_35](start_span)val mutableInteraction = remember { MutableInteractionSource() }[span_35](end_span)
    val selectedColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
        label = "selectedColor"
    )
    val selectedIconColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        [span_36](start_span)label = "selectedIconColor"[span_36](end_span)
    )
    Box(
        modifier = Modifier
            .height(64.dp)
            .weight(1f)
            .clickable(
                indication = null,
                [span_37](start_span)interactionSource = mutableInteraction,[span_37](end_span)
                onClick = {}
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .height(32.dp)
                [span_38](start_span).width(64.dp)[span_38](end_span)
                .background(
                    color = selectedColor,
                    shape = RoundedCornerShape(percent = 100)
                )
                [span_39](start_span).clip(RoundedCornerShape(100))[span_39](end_span)
                .clickable { if (!isSelected) onClick(navItem.route) },
        )
        Icon(
            modifier = Modifier
                .size(22.dp),
            imageVector = navItem.icon,
            [span_40](start_span)contentDescription = navItem.name,[span_40](end_span)
            tint = selectedIconColor
        )
    }
}

@Composable
fun GooglePhotosNavigationPill(
    navController: NavController,
    navItems: List<NavigationItem>,
    [span_41](start_span)currentBackStackEntry: NavBackStackEntry?[span_41](end_span)
) {
    [span_42](start_span)val currentRoute = currentBackStackEntry?.destination?.route[span_42](end_span)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
        [span_43](start_span)verticalAlignment = Alignment.CenterVertically[span_43](end_span)
    ) {
        // Main Pill Box Container
        Row(
            modifier = Modifier
                [span_44](start_span).clip(androidx.compose.foundation.shape.CircleShape)[span_44](end_span)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f))
                [span_45](start_span).padding(horizontal = 8.dp, vertical = 6.dp),[span_45](end_span)
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)
        ) {
            navItems.forEach { item ->
                [span_46](start_span)val isSelected = currentRoute == item.route[span_46](end_span)
                
                Row(
                    modifier = Modifier
                        [span_47](start_span).clip(androidx.compose.foundation.shape.CircleShape)[span_47](end_span)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primaryContainer 
                            [span_48](start_span)else Color.Transparent[span_48](end_span)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            [span_49](start_span)indication = null[span_49](end_span)
                        ) {
                            if (currentRoute != item.route) {
                                [span_50](start_span)navController.navigate(item.route) {[span_50](end_span)
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    [span_51](start_span)launchSingleTop = true[span_51](end_span)
                                    restoreState = true
                                }
                            [span_52](start_span)}
                        }
                        .padding(horizontal = if (isSelected) 18.dp else 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,[span_52](end_span)
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = item.icon,
                            [span_53](start_span)contentDescription = item.name,[span_53](end_span)
                            [span_54](start_span)tint = MaterialTheme.colorScheme.onPrimaryContainer,[span_54](end_span)
                            modifier = Modifier.size(18.dp)
                        )
                        [span_55](start_span)Spacer(modifier = Modifier.width(8.dp))[span_55](end_span)
                    }
                    
                    Text(
                        [span_56](start_span)text = item.name,[span_56](end_span)
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer 
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                        [span_57](start_span)fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,[span_57](end_span)
                        fontSize = 14.sp
                    )
                }
            [span_58](start_span)}
        }
        
        // Floating Utility Button on the right
        Spacer(modifier = Modifier.width(8.dp))[span_58](end_span)
        Box(
            modifier = Modifier
                [span_59](start_span).size(48.dp)[span_59](end_span)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f))
                .clickable {
                    [span_60](start_span)navController.navigate(Screen.SearchScreen.route)[span_60](end_span)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                [span_61](start_span)contentDescription = "Search",[span_61](end_span)
                tint = MaterialTheme.colorScheme.onSurfaceVaria
