package com.example.a0100_toefleptapp.view.uicontroller

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.a0100_toefleptapp.model.TestAttempt
import com.example.a0100_toefleptapp.view.*
import com.example.a0100_toefleptapp.viewmodel.*

sealed class Screen(val route: String, val title: String = "", val icon: ImageVector? = null) {
    object Login : Screen("login")
    object Register : Screen("register")

    // Bottom Nav Screens
    object Home : Screen("home", "Latihan", Icons.Default.MenuBook)
    object History : Screen("history", "Riwayat", Icons.Default.History)
    object Bookmarks : Screen("bookmarks", "Catatan", Icons.Default.Bookmark)
    object Certificates : Screen("certificates", "Sertifikat", Icons.Default.Folder)

    // Flow Screens
    object Learning : Screen("learning", "Modul Belajar", Icons.Default.School)
    object TensesLab : Screen("tenses_lab", "Lab Tenses", Icons.Default.Tune)

    object TestSession : Screen("test/{packageId}") {
        fun createRoute(packageId: String) = "test/$packageId"
    }
    object ResultSummary : Screen("result")
}

@Composable
fun Navigasi(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    testViewModel: TestViewModel,
    historyViewModel: HistoryViewModel,
    bookmarkViewModel: BookmarkViewModel,
    certViewModel: CertificateViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    val bottomNavItems = listOf(
        Screen.Home,
        Screen.History,
        Screen.Bookmarks,
        Screen.Certificates
    )

    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    // Hold last test attempt for Result screen
    var latestAttempt by remember { mutableStateOf<TestAttempt?>(null) }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            icon = { screen.icon?.let { Icon(it, contentDescription = screen.title) } },
                            label = { Text(screen.title) },
                            selected = selected,
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
            startDestination = if (authViewModel.checkLoginStatus()) Screen.Home.route else Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    viewModel = authViewModel,
                    onRegisterSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    currentUser = authViewModel.getCurrentUser(),
                    onSelectPackage = { packageId ->
                        navController.navigate(Screen.TestSession.createRoute(packageId))
                    },
                    onOpenLearning = {
                        navController.navigate(Screen.Learning.route)
                    },
                    onOpenTensesLab = {
                        navController.navigate(Screen.TensesLab.route)
                    },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Screen.TestSession.route,
                arguments = listOf(navArgument("packageId") { type = NavType.StringType })
            ) { backStackEntry ->
                val packageId = backStackEntry.arguments?.getString("packageId") ?: "test-1"
                TestSessionScreen(
                    packageId = packageId,
                    viewModel = testViewModel,
                    onTestFinished = { attemptId ->
                        // fetch attempt from viewModel state
                        val state = testViewModel.uiState.value
                        if (state is TestUiState.Submitted) {
                            latestAttempt = state.attempt
                            navController.navigate(Screen.ResultSummary.route) {
                                popUpTo(Screen.TestSession.route) { inclusive = true }
                            }
                        }
                    },
                    onExit = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.ResultSummary.route) {
                val attempt = latestAttempt
                if (attempt != null) {
                    ResultSummaryScreen(
                        attempt = attempt,
                        currentUser = authViewModel.getCurrentUser(),
                        certViewModel = certViewModel,
                        onBackToHome = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        },
                        onViewCertificates = {
                            navController.navigate(Screen.Certificates.route)
                        }
                    )
                } else {
                    navController.navigate(Screen.Home.route)
                }
            }

            composable(Screen.History.route) {
                HistoryScreen(
                    viewModel = historyViewModel,
                    onSelectAttempt = { attempt ->
                        latestAttempt = attempt
                        navController.navigate(Screen.ResultSummary.route)
                    }
                )
            }

            composable(Screen.Bookmarks.route) {
                BookmarkScreen(viewModel = bookmarkViewModel)
            }

            composable(Screen.Certificates.route) {
                CertificateScreen(
                    viewModel = certViewModel,
                    onBack = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Learning.route) {
                LearningModuleScreen(
                    onBack = { navController.popBackStack() },
                    onOpenTensesLab = { navController.navigate(Screen.TensesLab.route) }
                )
            }

            composable(Screen.TensesLab.route) {
                TensesLabScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

