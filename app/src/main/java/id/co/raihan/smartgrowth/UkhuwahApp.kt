package id.co.raihan.smartgrowth

import android.util.Log
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import id.co.raihan.smartgrowth.ui.components.BottomBar
import id.co.raihan.smartgrowth.ui.components.BottomBarParent
import id.co.raihan.smartgrowth.ui.screen.HistoryScreen
import id.co.raihan.smartgrowth.ui.screen.HomeParentScreen
import id.co.raihan.smartgrowth.ui.screen.HomeScreen
import id.co.raihan.smartgrowth.ui.screen.NotificationScreen
import id.co.raihan.smartgrowth.ui.screen.ProfileScreen
import id.co.raihan.smartgrowth.ui.screen.auth.ForgotPasswordScreen
import id.co.raihan.smartgrowth.ui.screen.auth.LoginScreen
import id.co.raihan.smartgrowth.ui.screen.auth.RegisterScreen
import id.co.raihan.smartgrowth.ui.screen.detail.DetailScreen
import id.co.raihan.smartgrowth.ui.screen.family.AddChildrenScreen
import id.co.raihan.smartgrowth.ui.screen.family.NewMeasureChildrenScreen
import id.co.raihan.smartgrowth.ui.screen.family.ParentScreen
import id.co.raihan.smartgrowth.ui.screen.family.ProfileChildrenScreen
import id.co.raihan.smartgrowth.ui.screen.family.ProfileParentScreen
import id.co.raihan.smartgrowth.ui.screen.health.DetailMpasiScreen
import id.co.raihan.smartgrowth.ui.screen.health.HealthScreen
import id.co.raihan.smartgrowth.ui.viewmodel.AuthViewModel

@Composable
fun UkhuwahApp(authViewModel: AuthViewModel) {


    val navController = rememberNavController()


    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(currentRoute) {
        Log.d("NAV_DEBUG", "Current route: $currentRoute")
    }

    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val userRole by authViewModel.userRole.collectAsState()
    Log.d("NAV_DEBUG", "Is logged in: $isLoggedIn , is role: $userRole")


    val isCheckingSession by authViewModel.isCheckingSession.collectAsState()

    val authFreeRoutes = listOf("login", "forgot-password")

    LaunchedEffect(isLoggedIn, userRole, isCheckingSession, currentRoute) {
        if (isCheckingSession) {
            return@LaunchedEffect
        }

        if (!isLoggedIn) {
            if (currentRoute !in authFreeRoutes) {
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            }
            return@LaunchedEffect
        }

        if (userRole == null) {
            Log.d("NAV_DEBUG", "Menunggu role...")
            return@LaunchedEffect
        }

        val target = if (userRole == "Parent") {
            "home_parent"
        } else {
            "home"
        }

        if (currentRoute == "login") {
            navController.navigate(target) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    val routes = setOf(
        "home",
        "detail",
        "history",
        "parent",
        "home_parent",
        "profile"
    )

    Scaffold(
        bottomBar = {
            if (currentRoute in routes) {
                when (userRole) {
                    "Admin" -> BottomBar(navController)
                    "Parent" -> BottomBarParent(navController)
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",

            ) {
//            Auth
            composable("login") {
                LoginScreen(
                    viewModel = authViewModel,
                    navController = navController
                )
            }
            composable("register") {
                RegisterScreen(
                    navController = navController,
                    authViewModel = authViewModel,
                    contentPadding = innerPadding
                )
            }
//            Kader & Orang tua
            composable(route = "detail") {
                DetailScreen(
                    navController = navController,
                    contentPadding = innerPadding,
                    authViewModel = authViewModel
                )
            }
            composable("history") {
                HistoryScreen(
                    navController = navController,
                    contentPadding = innerPadding,
                    authViewModel = authViewModel
                )
            }
            composable("health") {
                HealthScreen(
                    navController = navController,
                    contentPadding = innerPadding
                )
            }
            composable("notification") {
                NotificationScreen(
                    navController = navController,
                    contentPadding = innerPadding
                )
            }
            composable(
                route = "detail-mpasi/{id}",
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.IntType
                    }
                )

            ) {
                val id = it.arguments?.getInt("id")
                DetailMpasiScreen(
                    navController = navController,
                    contentPadding = innerPadding,
                    id = id ?: 0
                )
            }
            composable("forgot-password") {
                ForgotPasswordScreen(authViewModel)
            }
//            Parent
            composable("home_parent") {
                HomeParentScreen(
                    navController = navController,
                    contentPadding = innerPadding
                )
            }
            composable("profile") {
                ProfileScreen(
                    navController = navController,
                    paddingValues = innerPadding,
                    authViewModel = authViewModel
                )
            }
//            Kader
            composable("home") {
                HomeScreen(navController, authViewModel)
            }

            composable("add-children") {
                AddChildrenScreen(
                    navController = navController,
                    contentPadding = innerPadding
                )
            }
            composable("parent") {
                ParentScreen(
                    navController = navController,
                    contentPadding = innerPadding
                )
            }
            composable("update-children") {
                NewMeasureChildrenScreen(
                    navController = navController,
                    paddingvalues = innerPadding
                )
            }
            composable("profile-parent/{id}") {
                val id = it.arguments?.getString("id")
                ProfileParentScreen(
                    navController = navController,
                    idUser = id ?: "",
                    contentPadding = innerPadding,
                    authViewModel = authViewModel
                )
            }
            composable(
                route = "profile-children/{id}",
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.IntType
                    }
                )
            ) {
                val id = it.arguments?.getInt("id")
                ProfileChildrenScreen(
                    navController = navController,
                    contentPadding = innerPadding,
                    id = id ?: 0
                )
            }


        }
    }
}