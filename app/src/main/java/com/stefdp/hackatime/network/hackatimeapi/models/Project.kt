package com.stefdp.hackatime.network.hackatimeapi.models

import com.google.gson.annotations.SerializedName

data class Project(
    val archived: Boolean,
    val languages: List<String>,
    @SerializedName("most_recent_heartbeat") val mostRecentHeartbeat: String? = null,
    val name: String,
    @SerializedName("total_seconds") val totalSeconds: Long,
)

data class ProjectDetails(
    val archived: Boolean,
    @SerializedName("first_heartbeat") val firstHeartbeat: String? = null,
    val languages: List<String>,
    @SerializedName("last_heartbeat") val lastHeartbeat: String? = null,
    @SerializedName("most_recent_heartbeat") val mostRecentHeartbeat: String? = null,
    val name: String,
    @SerializedName("repo_url") val repoUrl: String? = null,
    @SerializedName("total_heartbeats") val totalHeartbeats: Long,
    @SerializedName("total_seconds") val totalSeconds: Long,
)