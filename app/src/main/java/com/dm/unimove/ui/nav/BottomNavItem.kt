package com.dm.unimove.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: Route,
    val title: String,
    val icon: ImageVector
) {
    object MapButton : BottomNavItem(Route.Map, "Mapa", Icons.Default.Place)
    object ListButton : BottomNavItem(Route.List, "Histórico de caronas", Icons.Default.List)
}