package com.stefdp.hackatime.network.backendapi.models.responses

import com.stefdp.hackatime.network.backendapi.models.Goal

data class GetUserGoalsResponse(
    val goals: List<Goal>
)
