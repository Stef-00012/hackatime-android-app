package com.stefdp.hackatime.screens

import kotlinx.serialization.Serializable

//@Serializable
//object LoginScreen
//
//@Serializable
//object HomeScreen
//
//@Serializable
//object SettingsScreen

@Serializable
sealed interface AppScreen

@Serializable
object LoginScreen : AppScreen

@Serializable
object HomeScreen : AppScreen

@Serializable
object ProjectsScreen : AppScreen

@Serializable
object GoalsScreen : AppScreen

@Serializable
object SettingsScreen : AppScreen