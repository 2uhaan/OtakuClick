package com.ruhaan.otakuclick.ui.components.bars

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp

// Navigation items data class
data class NavItem(
    val route: String,          // Navigation route
    val icon: ImageVector,      // Material icon
    val label: String          // Display name
)

// Bottom navigation bar component
@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    currentRoute: String,                    // Currently selected route
    onNavItemClick: (String) -> Unit        // Navigation callback
) {
    // Define all navigation items
    val navItems = listOf(
        NavItem("explore", Icons.Default.Explore, "Explore"),
        NavItem("categories", Icons.Default.Category, "Categories"),
        NavItem("schedule", Icons.Default.Schedule, "Schedule"),
        NavItem("club", Icons.Default.Business, "Clubs")
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,     // Dark surface color
        contentColor = MaterialTheme.colorScheme.onSurface     // Text color
    ) {
        navItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp                       // Smaller text for bottom nav
                    )
                },
                selected = currentRoute == item.route,         // Highlight current tab
                onClick = { onNavItemClick(item.route) },      // Handle tab clicks
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,     // Active icon color
                    selectedTextColor = MaterialTheme.colorScheme.primary,     // Active text color
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)  // Selection indicator
                )
            )
        }
    }
}