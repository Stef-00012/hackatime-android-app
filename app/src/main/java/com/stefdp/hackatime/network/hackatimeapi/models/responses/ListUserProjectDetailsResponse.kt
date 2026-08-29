package com.stefdp.hackatime.network.hackatimeapi.models.responses

import com.stefdp.hackatime.network.hackatimeapi.models.ProjectDetails

data class ListUserProjectDetailsResponse(
    val projects: List<ProjectDetails>
)