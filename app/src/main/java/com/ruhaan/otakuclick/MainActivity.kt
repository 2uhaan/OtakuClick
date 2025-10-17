package com.ruhaan.otakuclick

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ruhaan.otakuclick.data.models.AuthResult
import com.ruhaan.otakuclick.navigation.AppNavGraph
import com.ruhaan.otakuclick.navigation.Routes
import com.ruhaan.otakuclick.ui.components.bars.BottomNavBar
import com.ruhaan.otakuclick.ui.components.bars.TopSearchBar
import com.ruhaan.otakuclick.ui.screens.auth.AuthViewModel
import com.ruhaan.otakuclick.ui.screens.auth.LoginScreen
import com.ruhaan.otakuclick.ui.screens.auth.SignUpScreen
import com.ruhaan.otakuclick.ui.screens.auth.WelcomeScreen
import com.ruhaan.otakuclick.ui.screens.search.SearchViewModel
import com.ruhaan.otakuclick.ui.theme.OtakuClickTheme


@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Fix status bar colors
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }


        setContent {
            OtakuClickTheme {
                val authViewModel: AuthViewModel = viewModel()


                // Handle navigation from notification
                val navigateTo = intent.getStringExtra("navigate_to")


                when (authViewModel.authState) {
                    is AuthResult.Success -> {
                        // User is authenticated - show main app
                        MainApp(
                            authViewModel = authViewModel,
                            initialRoute = navigateTo // Pass the navigation intent
                        )
                    }
                    is AuthResult.Loading -> {
                        // Show loading screen while checking auth
                        AuthLoadingScreen()
                    }
                    else -> {
                        // User not authenticated - show auth flow
                        AuthApp(authViewModel = authViewModel)
                    }
                }
            }
        }
    }
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }

    // Handle new intents when app is already running
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        // If we get a navigation intent while app is running
        val navigateTo = intent.getStringExtra("navigate_to")
        if (navigateTo == "club") {
            // Handle navigation to club - you can implement this later if needed
            println("DEBUG: Received club navigation intent")
        }
    }
}




@Composable
fun MainApp(
    authViewModel: AuthViewModel,
    initialRoute: String? = null // Add initial route parameter
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Search state management
    val searchViewModel: SearchViewModel = viewModel()
    var searchText by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    // Handle navigation from notification
    LaunchedEffect(initialRoute) {
        if (initialRoute == "club") {
            // Navigate to club tab when coming from notification
            navController.navigate(Routes.CLUB) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    // Auto-close search when changing tabs
    LaunchedEffect(currentRoute) {
        if (currentRoute != Routes.EXPLORE) {
            isSearchActive = false
            searchText = ""
            searchViewModel.clearSearch()  // Add this method to SearchViewModel
        }
    }

    val showBottomNav = when {
        currentRoute == null -> true
        currentRoute.startsWith("anime_detail") -> true  // Changed to true (as discussed)
        currentRoute.startsWith("genre_detail") -> true
        else -> true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Enhanced top search bar
            TopSearchBar(
                searchText = searchText,
                onSearchTextChange = { searchText = it },
                onSearch = { query ->
                    if (query.isNotBlank()) {
                        searchViewModel.searchAnime(query)
                    }
                },
                isSearchActive = isSearchActive,
                onSearchActiveChange = { active ->
                    isSearchActive = active
                    if (!active) {
                        searchText = ""
                        searchViewModel.clearSearch()
                    }
                },
                searchResults = searchViewModel.animeList,
                isSearchLoading = searchViewModel.isLoading,
                searchError = searchViewModel.errorMessage,
                onAnimeClick = { animeId ->
                    navController.navigate(Routes.animeDetail(animeId))
                },
                onRetrySearch = {
                    if (searchText.isNotBlank()) {
                        searchViewModel.searchAnime(searchText)
                    }
                },
                onLogout = {  // Add logout handling
                    authViewModel.logout()
                }
            )

            // Main content area
            Box(modifier = Modifier.weight(1f)) {
                AppNavGraph(
                    navController = navController,
                    isSearchActive = isSearchActive,
                    authViewModel = authViewModel,  // Pass auth view model
                    modifier = Modifier.fillMaxSize()
                )
            }


            // Bottom navigation (conditional)
            if (showBottomNav) {
                BottomNavBar(
                    currentRoute = currentRoute ?: Routes.EXPLORE,
                    onNavItemClick = { route ->
                        navController.navigate(route) {
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
}


// New: Authentication app flow
@Composable
fun AuthApp(authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.WELCOME
    ) {
        composable(Routes.WELCOME) {
            WelcomeScreen(
                onLoginClick = {
                    navController.navigate(Routes.LOGIN)
                },
                onSignUpClick = {
                    navController.navigate(Routes.SIGNUP)
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onSignUpClick = {
                    navController.navigate(Routes.SIGNUP) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onLoginSuccess = {
                    // Success handled by MainActivity state change
                }
            )
        }

        composable(Routes.SIGNUP) {
            SignUpScreen(
                viewModel = authViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onLoginClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SIGNUP) { inclusive = true }
                    }
                },
                onSignUpSuccess = {
                    // Success handled by MainActivity state change
                }
            )
        }
    }
}

// Loading screen while checking authentication
@Composable
fun AuthLoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading...",
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

