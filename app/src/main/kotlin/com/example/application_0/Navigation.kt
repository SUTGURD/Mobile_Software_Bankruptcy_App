package com.example.application_0

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

enum class AppRoute(val route: String, val label: String, val hasBackButton: Boolean) {
    Entry("entry", "Entry", false),
    Login("login", "Log in", true),
    CreateAccount("create-account", "Create account", true),
    Home("home", "Home", false),
    Groups("groups", "Groups", true),
    Debt("debt", "Debt", true),
    Notifications("notifications", "Notifications", true),
    Profile("profile", "Profile", true),
    CreateGroup("create-group", "Create group", true),
    GroupDetails("group-details", "Group details", true),
    Members("members", "Members", true),
    ManageMembers("manage-members", "Manage members", true),
    Expenses("expenses", "Expenses", true),
    AddExpense("add-expense", "Add expense", true),
    DebtSummary("debt-summary", "Debt summary", true)
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    var backEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(currentRoute) {
        val destination = AppRoute.entries.firstOrNull { it.route == currentRoute }
        if (destination?.hasBackButton == true) {
            backEnabled = true
        }
    }

    val navigate: (AppRoute) -> Unit = { destination ->
        if (destination != AppRoute.Home || Authentication.currentUser.value != null) {
            backEnabled = false
            navController.navigate(destination.route) {
                launchSingleTop = true
            }
        }
    }
    val navigateToHome: () -> Unit = {
        if (Authentication.currentUser.value != null) {
            backEnabled = false
            navController.navigate(AppRoute.Home.route) {
                popUpTo(AppRoute.Entry.route) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }
    val goBack: () -> Unit = {
        if (backEnabled) {
            backEnabled = false
            navController.popBackStack()
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppRoute.Entry.route
    ) {
        composable(AppRoute.Entry.route) {
            EntryScreen(onNavigate = navigate)
        }
        composable(AppRoute.Login.route) {
            LoginScreen(
                onNavigate = navigate,
                onLogin = Authentication::authenticate,
                onAuthenticated = navigateToHome,
                onBack = goBack,
                backEnabled = backEnabled
            )
        }
        composable(AppRoute.CreateAccount.route) {
            CreateAccountScreen(
                onNavigate = navigate,
                onBack = goBack,
                backEnabled = backEnabled
            )
        }
        composable(AppRoute.Home.route) {
            val authenticatedUser by Authentication.currentUser.collectAsState()
            LaunchedEffect(authenticatedUser) {
                if (authenticatedUser == null) {
                    navController.navigate(AppRoute.Entry.route) {
                        popUpTo(AppRoute.Home.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            }
            if (authenticatedUser != null) {
                HomeScreen(onNavigate = navigate)
            }
        }
        composable(AppRoute.Groups.route) {
            GroupsScreen(onNavigate = navigate, onBack = goBack, backEnabled = backEnabled)
        }
        composable(AppRoute.Debt.route) {
            DebtScreen(onNavigate = navigate, onBack = goBack, backEnabled = backEnabled)
        }
        composable(AppRoute.Notifications.route) {
            NotificationsScreen(onNavigate = navigate, onBack = goBack, backEnabled = backEnabled)
        }
        composable(AppRoute.Profile.route) {
            ProfileScreen(onNavigate = navigate, onBack = goBack, backEnabled = backEnabled)
        }
        composable(AppRoute.CreateGroup.route) {
            CreateGroupScreen(onNavigate = navigate, onBack = goBack, backEnabled = backEnabled)
        }
        composable(AppRoute.GroupDetails.route) {
            GroupDetailsScreen(onNavigate = navigate, onBack = goBack, backEnabled = backEnabled)
        }
        composable(AppRoute.Members.route) {
            MembersScreen(onNavigate = navigate, onBack = goBack, backEnabled = backEnabled)
        }
        composable(AppRoute.ManageMembers.route) {
            ManageMembersScreen(onBack = goBack, backEnabled = backEnabled)
        }
        composable(AppRoute.Expenses.route) {
            ExpensesScreen(onNavigate = navigate, onBack = goBack, backEnabled = backEnabled)
        }
        composable(AppRoute.AddExpense.route) {
            AddExpenseScreen(onBack = goBack, backEnabled = backEnabled)
        }
        composable(AppRoute.DebtSummary.route) {
            DebtSummaryScreen(onNavigate = navigate, onBack = goBack, backEnabled = backEnabled)
        }
    }
}
