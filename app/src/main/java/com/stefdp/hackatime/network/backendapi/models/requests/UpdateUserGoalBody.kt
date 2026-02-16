package com.stefdp.hackatime.network.backendapi.models.requests

import com.stefdp.hackatime.network.backendapi.models.GoalDate

data class UpdateUserGoalBody(
    val goal: Long,
    val date: GoalDate
)
