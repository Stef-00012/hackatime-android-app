package com.stefdp.hackatime.network.backendapi.models

data class Goal(
    val date: String,
    val achieved: Double,
    val goal: Double,
)

typealias GoalDate = String