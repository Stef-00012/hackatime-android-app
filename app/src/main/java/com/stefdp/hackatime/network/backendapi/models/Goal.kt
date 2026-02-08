package com.stefdp.hackatime.network.backendapi.models

data class Goal(
    val date: String,
    val achieved: Boolean,
    val goal: Int,
)

typealias GoalDate = String