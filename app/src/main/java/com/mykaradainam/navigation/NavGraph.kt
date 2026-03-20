// navigation/NavGraph.kt
package com.mykaradainam.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mykaradainam.ui.home.HomeScreen
import com.mykaradainam.ui.invoice.CameraScreen
import com.mykaradainam.ui.invoice.VoiceScreen
import com.mykaradainam.ui.invoice.ConfirmScreen
import com.mykaradainam.ui.reports.ReportsScreen
import com.mykaradainam.ui.settings.SettingsScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.Home,
        enterTransition = { fadeIn(tween(200)) + slideInHorizontally { it / 4 } },
        exitTransition = { fadeOut(tween(200)) },
        popEnterTransition = { fadeIn(tween(200)) + slideInHorizontally { -it / 4 } },
        popExitTransition = { fadeOut(tween(200)) }
    ) {
        composable<Route.Home> {
            HomeScreen(
                onNavigateToCamera = { sessionId, roomNum ->
                    navController.navigate(Route.Camera(sessionId, roomNum))
                },
                onNavigateToVoice = { sessionId, roomNum ->
                    navController.navigate(Route.Voice(sessionId, roomNum))
                },
                onNavigateToReports = {
                    navController.navigate(Route.Reports)
                },
                onNavigateToSettings = {
                    navController.navigate(Route.Settings)
                }
            )
        }

        composable<Route.Camera> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.Camera>()
            CameraScreen(
                sessionId = route.sessionId,
                roomNumber = route.roomNumber,
                onNavigateToConfirm = {
                    navController.navigate(Route.Confirm(route.sessionId, route.roomNumber, "camera")) {
                        popUpTo<Route.Camera> { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable<Route.Voice> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.Voice>()
            VoiceScreen(
                sessionId = route.sessionId,
                roomNumber = route.roomNumber,
                onNavigateToConfirm = {
                    navController.navigate(Route.Confirm(route.sessionId, route.roomNumber, "voice")) {
                        popUpTo<Route.Voice> { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable<Route.Confirm> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.Confirm>()
            ConfirmScreen(
                sessionId = route.sessionId,
                roomNumber = route.roomNumber,
                onSaved = {
                    navController.navigate(Route.Home) {
                        popUpTo<Route.Home> { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable<Route.Reports> {
            ReportsScreen(onBack = { navController.popBackStack() })
        }

        composable<Route.Settings> {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
