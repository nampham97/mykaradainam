// navigation/Routes.kt
package com.mykaradainam.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable data object Home : Route
    @Serializable data class Camera(val sessionId: Long, val roomNumber: Int) : Route
    @Serializable data class Voice(val sessionId: Long, val roomNumber: Int) : Route
    @Serializable data class Confirm(val sessionId: Long, val roomNumber: Int, val source: String) : Route
    @Serializable data object Reports : Route
    @Serializable data object Settings : Route
}
