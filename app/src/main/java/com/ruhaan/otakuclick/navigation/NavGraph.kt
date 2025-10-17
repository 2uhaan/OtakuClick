package com.ruhaan.otakuclick.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ruhaan.otakuclick.data.models.AuthResult
import com.ruhaan.otakuclick.ui.screens.auth.AuthViewModel
import com.ruhaan.otakuclick.ui.screens.auth.LoginScreen
import com.ruhaan.otakuclick.ui.screens.auth.SignUpScreen
import com.ruhaan.otakuclick.ui.screens.auth.WelcomeScreen
import com.ruhaan.otakuclick.ui.screens.categories.CategoriesScreen
import com.ruhaan.otakuclick.ui.screens.categories.GenreDetailScreen
import com.ruhaan.otakuclick.ui.screens.club.ClubScreen
import com.ruhaan.otakuclick.ui.screens.detail.AnimeDetailScreen
import com.ruhaan.otakuclick.ui.screens.explore.ExploreScreen
import com.ruhaan.otakuclick.ui.screens.schedule.ScheduleScreen


@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel,  // Add this parameter
    isSearchActive: Boolean = false,         // Add search state parameter
) {

//    val authViewModel: AuthViewModel = viewModel()

    val authState = authViewModel.authState

    val startDestination = if (authState is AuthResult.Success) {
        Routes.EXPLORE
    } else {
        Routes.WELCOME
    }




    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Authentication screens
        composable(
            Routes.WELCOME,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            WelcomeScreen(
                onLoginClick = {
                    navController.navigate(Routes.LOGIN)
                },
                onSignUpClick = {
                    navController.navigate(Routes.SIGNUP)
                }
            )
        }

        composable(
            Routes.LOGIN,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) +
                        fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) +
                        fadeOut(animationSpec = tween(300))
            }
        ) {
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
                    navController.navigate(Routes.EXPLORE) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                }
            )
        }

        composable(
            Routes.SIGNUP,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) +
                        fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) +
                        fadeOut(animationSpec = tween(300))
            }
        ) {
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
                    navController.navigate(Routes.EXPLORE) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                }
            )
        }

        // Main bottom navigation screens
        composable(
            Routes.EXPLORE,
            enterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) }
        ) {
            ExploreScreen(
                onAnimeClick = { animeId ->
                    navController.navigate(Routes.animeDetail(animeId))
                },
                isSearchActive = isSearchActive   // Use passed ViewModel or create new
            )
        }

        composable(
            Routes.CATEGORIES,
            enterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) }
        ) {
            CategoriesScreen(
                onGenreClick = { genreId, genreName ->
                    navController.navigate(Routes.genreDetail(genreId, genreName))
                }
            )
        }

        composable(
            Routes.SCHEDULE,
            enterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) }
        ) {
            ScheduleScreen(
                onAnimeClick = { animeId ->
                    navController.navigate(Routes.animeDetail(animeId))
                }
            )
        }

        // Update Club route to pass auth view model
        composable(Routes.CLUB) {
            ClubScreen(
                authViewModel = authViewModel  // Pass to Club screen
            )
        }

        // Anime detail screen (from Project 2)
        composable(
            Routes.ANIME_DETAIL,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) +
                        fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) +
                        fadeOut(animationSpec = tween(300))
            }
        ) { backStackEntry ->
            val animeId = backStackEntry.arguments?.getString("animeId")?.toIntOrNull() ?: 0
            AnimeDetailScreen(
                animeId = animeId,
                onBackClick = {
                    navController.popBackStack()
                },
                onAnimeClick = { newAnimeId ->
                    navController.navigate(Routes.animeDetail(newAnimeId))
                }
            )
        }

        composable(
            Routes.GENRE_DETAIL,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) +
                        fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) +
                        fadeOut(animationSpec = tween(300))
            }
        ) { backStackEntry ->
            val genreId = backStackEntry.arguments?.getString("genreId")?.toIntOrNull() ?: 0
            val genreName = backStackEntry.arguments?.getString("genreName") ?: ""

            GenreDetailScreen(
                genreId = genreId,
                genreName = genreName,
                onBackClick = {
                    navController.popBackStack()
                },
                onAnimeClick = { animeId ->
                    navController.navigate(Routes.animeDetail(animeId))
                }
            )
        }

    }
}

