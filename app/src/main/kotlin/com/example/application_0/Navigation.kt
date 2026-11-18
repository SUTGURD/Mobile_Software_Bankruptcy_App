package com.example.application_0

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

enum class AppRoute(val route: String, val label: String, val hasBackButton: Boolean) {
    Entry("entry", "Entry", false),
    Login("login", "Log in", true),
    CreateAccount("create-account", "Create account", true),
    Home("home", "Home", false),
    Groups("groups", "Groups", false),
    Debt("debt", "Debt", false),
    Notifications("notifications", "Notifications", true),
    Profile("profile", "Profile", false),
    CreateGroup("create-group", "Create group", true),
    GroupDetails("group-details", "Group details", true),
    Members("members", "Members", true),
    ManageMembers("manage-members", "Manage members", true),
    Expenses("expenses", "Expenses", true),
    AddExpense("add-expense", "Add expense", true),
    DebtSummary("debt-summary", "Debt summary", true)
}

private val topLevelRoutes = listOf(AppRoute.Home, AppRoute.Groups, AppRoute.Debt, AppRoute.Profile)
private val publicRoutes = setOf(AppRoute.Entry, AppRoute.Login, AppRoute.CreateAccount)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = AppRoute.entries.firstOrNull {
        it.route == currentBackStackEntry?.destination?.route
    }
    val authenticatedUser by Authentication.currentUser.collectAsState()
    val currentRouteState = rememberUpdatedState(currentRoute)
    val authenticatedUserState = rememberUpdatedState(authenticatedUser)
    var selectedMenu by remember { mutableStateOf(AppRoute.Home) }
    var menuStacks by remember { mutableStateOf(initialMenuStacks()) }
    var notificationReturnMenu by remember { mutableStateOf(AppRoute.Home) }
    var notificationReturnRoute by remember { mutableStateOf(AppRoute.Home) }
    var backEnabled by remember { mutableStateOf(false) }

    fun resetAuthenticatedNavigation() {
        selectedMenu = AppRoute.Home
        menuStacks = initialMenuStacks()
        notificationReturnMenu = AppRoute.Home
        notificationReturnRoute = AppRoute.Home
        ActiveGroup.clear()
    }

    fun showRoute(destination: AppRoute) {
        navigateWithinAuthenticatedGraph(navController, destination)
    }

    fun navigate(destination: AppRoute) {
        val routeNow = currentRouteState.value
        when {
            destination == AppRoute.Entry -> {
                resetAuthenticatedNavigation()
                showRoute(AppRoute.Entry)
            }
            destination == AppRoute.Login || destination == AppRoute.CreateAccount -> {
                navController.navigate(destination.route) {
                    launchSingleTop = true
                }
            }
            authenticatedUserState.value == null -> Unit
            destination == AppRoute.Notifications -> {
                if (routeNow != AppRoute.Notifications) {
                    notificationReturnMenu = selectedMenu
                    notificationReturnRoute = routeNow
                        ?.takeIf { it !in publicRoutes && it != AppRoute.Notifications }
                        ?: selectedMenu
                }
                showRoute(AppRoute.Notifications)
            }
            destination in topLevelRoutes -> {
                selectedMenu = destination
                showRoute(menuStacks[destination]?.lastOrNull() ?: destination)
            }
            else -> {
                val destinationMenu = menuFor(destination)
                val currentMenu = selectedMenu
                val currentRouteIndex = menuStacks[destinationMenu]
                    ?.indexOf(routeNow)
                    ?: -1

                menuStacks = menuStacks + (
                    destinationMenu to if (routeNow == AppRoute.Notifications ||
                        destinationMenu != currentMenu ||
                        currentRouteIndex < 0
                    ) {
                        canonicalPath(destination)
                    } else {
                        val existingIndex = menuStacks[destinationMenu]
                            .orEmpty()
                            .indexOf(destination)
                        if (existingIndex >= 0) {
                            menuStacks.getValue(destinationMenu).take(existingIndex + 1)
                        } else {
                            menuStacks.getValue(destinationMenu) + destination
                        }
                    }
                )
                selectedMenu = destinationMenu
                showRoute(destination)
            }
        }
    }

    fun goBack() {
        if (!backEnabled) return
        backEnabled = false

        val routeNow = currentRouteState.value
        when {
            routeNow == AppRoute.Notifications -> {
                selectedMenu = notificationReturnMenu
                showRoute(notificationReturnRoute)
            }
            routeNow != null && routeNow in publicRoutes -> navController.popBackStack()
            routeNow != null -> {
                val stack = menuStacks[selectedMenu].orEmpty()
                val index = stack.indexOf(routeNow)
                if (index > 0) {
                    val previousStack = stack.take(index)
                    menuStacks = menuStacks + (selectedMenu to previousStack)
                    showRoute(previousStack.last())
                }
            }
        }
    }

    LaunchedEffect(currentRoute) {
        backEnabled = currentRoute?.hasBackButton == true
    }

    LaunchedEffect(authenticatedUser?.userId) {
        if (authenticatedUser == null) {
            resetAuthenticatedNavigation()
            if (currentRoute == null || currentRoute !in publicRoutes) {
                showRoute(AppRoute.Entry)
            }
        } else {
            resetAuthenticatedNavigation()
        }
    }

    BackHandler(enabled = authenticatedUser != null) {
        goBack()
    }

    NavHost(
        navController = navController,
        startDestination = AppRoute.Entry.route
    ) {
        composable(AppRoute.Entry.route) {
            EntryScreen(onNavigate = ::navigate)
        }
        composable(AppRoute.Login.route) {
            LoginScreen(
                onNavigate = ::navigate,
                onLogin = Authentication::authenticate,
                onAuthenticated = {
                    resetAuthenticatedNavigation()
                    showRoute(AppRoute.Home)
                },
                onBack = ::goBack,
                backEnabled = backEnabled
            )
        }
        composable(AppRoute.CreateAccount.route) {
            CreateAccountScreen(
                onNavigate = ::navigate,
                onBack = ::goBack,
                backEnabled = backEnabled
            )
        }
        composable(AppRoute.Home.route) {
            if (authenticatedUser != null) {
                HomeScreen(onNavigate = ::navigate)
            }
        }
        composable(AppRoute.Groups.route) {
            GroupsScreen(
                onNavigate = ::navigate,
                onBack = null,
                backEnabled = backEnabled
            )
        }
        composable(AppRoute.Debt.route) {
            DebtScreen(
                onNavigate = ::navigate,
                onBack = null,
                backEnabled = backEnabled
            )
        }
        composable(AppRoute.Notifications.route) {
            NotificationsScreen(
                onNavigate = ::navigate,
                onBack = ::goBack,
                backEnabled = backEnabled,
                selectedMenu = selectedMenu
            )
        }
        composable(AppRoute.Profile.route) {
            ProfileScreen(
                onNavigate = ::navigate,
                onBack = null,
                backEnabled = backEnabled
            )
        }
        composable(AppRoute.CreateGroup.route) {
            CreateGroupScreen(onNavigate = ::navigate, onBack = ::goBack, backEnabled = backEnabled)
        }
        composable(AppRoute.GroupDetails.route) {
            GroupDetailsScreen(onNavigate = ::navigate, onBack = ::goBack, backEnabled = backEnabled)
        }
        composable(AppRoute.Members.route) {
            MembersScreen(onNavigate = ::navigate, onBack = ::goBack, backEnabled = backEnabled)
        }
        composable(AppRoute.ManageMembers.route) {
            ManageMembersScreen(onNavigate = ::navigate, onBack = ::goBack, backEnabled = backEnabled)
        }
        composable(AppRoute.Expenses.route) {
            ExpensesScreen(onNavigate = ::navigate, onBack = ::goBack, backEnabled = backEnabled)
        }
        composable(AppRoute.AddExpense.route) {
            AddExpenseScreen(onNavigate = ::navigate, onBack = ::goBack, backEnabled = backEnabled)
        }
        composable(AppRoute.DebtSummary.route) {
            DebtSummaryScreen(onNavigate = ::navigate, onBack = ::goBack, backEnabled = backEnabled)
        }
    }
}

private fun initialMenuStacks(): Map<AppRoute, List<AppRoute>> =
    topLevelRoutes.associateWith { listOf(it) }

private fun menuFor(destination: AppRoute): AppRoute = when (destination) {
    AppRoute.Home -> AppRoute.Home
    AppRoute.Debt -> AppRoute.Debt
    AppRoute.Profile -> AppRoute.Profile
    else -> AppRoute.Groups
}

private fun canonicalPath(destination: AppRoute): List<AppRoute> = when (destination) {
    AppRoute.Home, AppRoute.Groups, AppRoute.Debt, AppRoute.Profile -> listOf(destination)
    AppRoute.CreateGroup -> listOf(AppRoute.Groups, AppRoute.CreateGroup)
    AppRoute.GroupDetails -> listOf(AppRoute.Groups, AppRoute.GroupDetails)
    AppRoute.Members -> listOf(AppRoute.Groups, AppRoute.GroupDetails, AppRoute.Members)
    AppRoute.ManageMembers -> listOf(
        AppRoute.Groups,
        AppRoute.GroupDetails,
        AppRoute.Members,
        AppRoute.ManageMembers
    )
    AppRoute.Expenses -> listOf(AppRoute.Groups, AppRoute.GroupDetails, AppRoute.Expenses)
    AppRoute.AddExpense -> listOf(
        AppRoute.Groups,
        AppRoute.GroupDetails,
        AppRoute.Expenses,
        AppRoute.AddExpense
    )
    AppRoute.DebtSummary -> listOf(AppRoute.Groups, AppRoute.GroupDetails, AppRoute.DebtSummary)
    else -> listOf(menuFor(destination), destination)
}

private fun navigateWithinAuthenticatedGraph(
    navController: NavHostController,
    destination: AppRoute
) {
    navController.navigate(destination.route) {
        popUpTo(AppRoute.Entry.route) {
            inclusive = false
        }
        launchSingleTop = true
    }
}
