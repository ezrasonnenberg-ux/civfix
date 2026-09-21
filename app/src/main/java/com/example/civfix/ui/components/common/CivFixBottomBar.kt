package com.example.civfix.ui.components.common

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// ==========================================
// MARK: - Navigation Item Data Class
// ==========================================

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Feed : Screen("feed", "Feed", Icons.Default.Home)
    object Map : Screen("map", "Map", Icons.Default.Map)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
}

// ==========================================
// MARK: - Reusable CivFix Bottom Navigation Bar
// ==========================================

@Composable
fun CivFixBottomBar(
    currentRoute: String,
    onItemClicked: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        Screen.Feed,
        Screen.Map,
        Screen.Profile
    )

    val primaryBlue = Color(0xFF004AAD)
    val inactiveGray = Color.Gray

    NavigationBar(
        modifier = modifier,
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        items.forEach { screen ->
            val isSelected = (currentRoute == screen.route)

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(text = screen.title)
                },
                selected = isSelected,
                onClick = { onItemClicked(screen) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = primaryBlue,
                    selectedTextColor = primaryBlue,
                    unselectedIconColor = inactiveGray,
                    unselectedTextColor = inactiveGray,
                    indicatorColor = Color(0xFFF0F4F8) // Soft highlight background for active tab
                )
            )
        }
    }
}