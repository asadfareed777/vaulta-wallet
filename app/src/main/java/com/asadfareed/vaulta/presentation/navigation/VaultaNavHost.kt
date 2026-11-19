package com.asadfareed.vaulta.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.asadfareed.vaulta.core.designsystem.VaultaColors
import com.asadfareed.vaulta.presentation.cards.CardsRoute
import com.asadfareed.vaulta.presentation.home.HomeRoute
import com.asadfareed.vaulta.presentation.insights.InsightsRoute
import com.asadfareed.vaulta.presentation.transfer.TransferRoute

object Routes {
    const val HOME = "home"
    const val INSIGHTS = "insights"
    const val CARDS = "cards"
    const val TRANSFER = "transfer"
}

private enum class TopLevel(val route: String, val label: String, val icon: ImageVector) {
    Home(Routes.HOME, "Home", Icons.Rounded.Home),
    Insights(Routes.INSIGHTS, "Insights", Icons.Rounded.Insights),
    Cards(Routes.CARDS, "Cards", Icons.Rounded.CreditCard),
}

@Composable
fun VaultaNavHost() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val destination = backStack?.destination
    val showBottomBar = TopLevel.entries.any { it.route == destination?.route }

    fun navigateTopLevel(route: String) = navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }

    Scaffold(
        containerColor = VaultaColors.Ink,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = VaultaColors.Surface) {
                    TopLevel.entries.forEach { item ->
                        val selected = destination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = { navigateTopLevel(item.route) },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = VaultaColors.Ink,
                                indicatorColor = VaultaColors.Mint,
                                selectedTextColor = VaultaColors.Mint,
                                unselectedIconColor = VaultaColors.TextSecondary,
                                unselectedTextColor = VaultaColors.TextSecondary,
                            ),
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding),
        ) {
            composable(Routes.HOME) {
                HomeRoute(
                    onSendMoney = { navController.navigate(Routes.TRANSFER) },
                    onSeeInsights = { navigateTopLevel(Routes.INSIGHTS) },
                )
            }
            composable(Routes.INSIGHTS) { InsightsRoute() }
            composable(Routes.CARDS) { CardsRoute() }
            composable(Routes.TRANSFER) { TransferRoute(onBack = { navController.popBackStack() }) }
        }
    }
}
