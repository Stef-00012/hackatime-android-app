package com.stefdp.hackatime.network.models.responses

import com.google.gson.annotations.SerializedName

data class UserProjectDetailsResponse(
    val projects: List<ProjectDetail>
)

data class ProjectDetail(
    val name: String,
    @SerializedName("total_seconds") val totalSeconds: Double,
    val languages: List<String>,
    @SerializedName("repo_url") val repoUrl: String?,
    @SerializedName("total_heartbeats") val totalHeartbeats: Int,
    @SerializedName("first_heartbeat") val firstHeartbeat: String,
    @SerializedName("last_heartbeat") val lastHeartbeat: String,
)