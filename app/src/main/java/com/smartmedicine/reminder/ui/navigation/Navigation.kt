package com.smartmedicine.reminder.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.smartmedicine.reminder.ui.screens.AddHealthRecordScreen
import com.smartmedicine.reminder.ui.screens.AddMedicineScreen
import com.smartmedicine.reminder.ui.screens.CaregiverScreen
import com.smartmedicine.reminder.ui.screens.DashboardScreen
import com.smartmedicine.reminder.ui.screens.HealthTrackingScreen
import com.smartmedicine.reminder.ui.screens.MedicineListScreen
import com.smartmedicine.reminder.viewmodel.CaregiverViewModel
import com.smartmedicine.reminder.viewmodel.HealthViewModel
import com.smartmedicine.reminder.viewmodel.MedicineViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    data object Medicines : Screen("medicines", "Medicines", Icons.Default.Medication)
    data object Health : Screen("health", "Health", Icons.Default.Favorite)
    data object Caregivers : Screen("caregivers", "Caregivers", Icons.Default.People)
}

sealed class DetailScreen(val route: String) {
    data object AddMedicine : DetailScreen("add_medicine")
    data object EditMedicine : DetailScreen("edit_medicine/{medicineId}")
    data object AddHealthRecord : DetailScreen("add_health_record/{recordType}")
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val medicineViewModel: MedicineViewModel = viewModel()
    val healthViewModel: HealthViewModel = viewModel()
    val caregiverViewModel: CaregiverViewModel = viewModel()

    val bottomNavItems = listOf(
        Screen.Dashboard,
        Screen.Medicines,
        Screen.Health,
        Screen.Caregivers
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Show bottom bar only on main screens
    val showBottomBar = bottomNavItems.any { screen ->
        currentDestination?.hierarchy?.any { it.route == screen.route } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    screen.icon,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontSize = 13.sp
                                )
                            },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
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
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    medicineViewModel = medicineViewModel,
                    healthViewModel = healthViewModel,
                    onNavigateToMedicines = {
                        navController.navigate(Screen.Medicines.route)
                    },
                    onNavigateToHealth = {
                        navController.navigate(Screen.Health.route)
                    }
                )
            }

            composable(Screen.Medicines.route) {
                MedicineListScreen(
                    viewModel = medicineViewModel,
                    onAddMedicine = {
                        navController.navigate(DetailScreen.AddMedicine.route)
                    },
                    onEditMedicine = { medicineId ->
                        navController.navigate("edit_medicine/$medicineId")
                    }
                )
            }

            composable(Screen.Health.route) {
                HealthTrackingScreen(
                    viewModel = healthViewModel,
                    onAddRecord = { type ->
                        navController.navigate("add_health_record/$type")
                    }
                )
            }

            composable(Screen.Caregivers.route) {
                CaregiverScreen(viewModel = caregiverViewModel)
            }

            composable(DetailScreen.AddMedicine.route) {
                AddMedicineScreen(
                    viewModel = medicineViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = DetailScreen.EditMedicine.route,
                arguments = listOf(navArgument("medicineId") { type = NavType.LongType })
            ) { backStackEntry ->
                val medicineId = backStackEntry.arguments?.getLong("medicineId") ?: -1L
                AddMedicineScreen(
                    viewModel = medicineViewModel,
                    medicineId = medicineId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = DetailScreen.AddHealthRecord.route,
                arguments = listOf(navArgument("recordType") { type = NavType.StringType })
            ) { backStackEntry ->
                val recordType = backStackEntry.arguments?.getString("recordType") ?: ""
                AddHealthRecordScreen(
                    viewModel = healthViewModel,
                    recordType = recordType,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
