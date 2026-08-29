package com.stefdp.hackatime.network.hackatimeapi.models.responses

import com.google.gson.annotations.SerializedName
import com.stefdp.hackatime.network.hackatimeapi.models.Project

data class ListOAuthUserProjectsResponse(
    val projects: List<Project>
)
