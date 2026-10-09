package com.dicoding.skripsirevisi.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dicoding.skripsirevisi.R
import com.dicoding.skripsirevisi.navigation.BottomBarItem
import com.dicoding.skripsirevisi.navigation.provideNavController
import com.dicoding.skripsirevisi.ui.theme.MainBlack
import com.dicoding.skripsirevisi.ui.theme.MainPink
import com.dicoding.skripsirevisi.ui.theme.SkripsiRevisiTheme

@Composable
fun BottomBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navigationItems = listOf(
        BottomBarItem(
            title = stringResource(R.string.home),
            icon = ImageVector.vectorResource(id = R.drawable.icon_home),
            route = "HomeScreen"
        ),
        BottomBarItem(
            title = stringResource(R.string.history),
            icon = ImageVector.vectorResource(id = R.drawable.icon_history),
            route = "HistoryScreen"
        ),
        BottomBarItem(
            title = stringResource(R.string.search),
            icon = ImageVector.vectorResource(id = R.drawable.icon_search),
            route = "SearchScreen"
        ),
        BottomBarItem(
            title = stringResource(R.string.profile),
            icon = ImageVector.vectorResource(id = R.drawable.icon_profile),
            route = "ProfileScreen"
        )
    )

    val currentRoute = navController.currentDestination?.route

    NavigationBar(
        modifier = modifier.padding(top = 16.dp)
    ) {
        navigationItems.forEach { item ->
            val isSelected = item.route == currentRoute
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) MainPink else MainBlack
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        color = if (isSelected) MainPink else MainBlack
                    )
                },
                selected = item.route == currentRoute,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}


@Composable
fun BottomBarAdmin(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navigationItems = listOf(
        BottomBarItem(
            title = stringResource(R.string.product),
            icon = ImageVector.vectorResource(id = R.drawable.icon_product),
            route = "NavProductScreen"
        ),
        BottomBarItem(
            title = stringResource(R.string.category),
            icon = ImageVector.vectorResource(id = R.drawable.icon_category),
            route = "NavCategoryScreen"
        ),
        BottomBarItem(
            title = stringResource(R.string.user),
            icon = ImageVector.vectorResource(id = R.drawable.icon_users),
            route = "NavUsersScreen"
        ),
        BottomBarItem(
            title = stringResource(R.string.profile),
            icon = ImageVector.vectorResource(id = R.drawable.icon_profile),
            route = "NavAdminProfileScreen"
        )
    )

    val currentRoute = navController.currentDestination?.route

    NavigationBar(
        modifier = modifier.padding(top = 16.dp)
    ) {
        navigationItems.forEach { item ->
            val isSelected = item.route == currentRoute
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) MainPink else MainBlack
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        color = if (isSelected) MainPink else MainBlack
                    )
                },
                selected = item.route == currentRoute,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}


@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun BottomNavPreview() {
    SkripsiRevisiTheme {
        Column {
            BottomBar(provideNavController())
        }
    }
}