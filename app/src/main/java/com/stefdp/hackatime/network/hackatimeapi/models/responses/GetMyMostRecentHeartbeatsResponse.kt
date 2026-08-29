package com.stefdp.hackatime.network.hackatimeapi.models.responses

import com.google.gson.annotations.SerializedName

data class GetMyMostRecentHeartbeatsResponse(
    val editor: String? = null,
    @SerializedName("has_heartbeat") val hasHeartbeat: Boolean,
    val heartbeat: Heartbeat? = null,
    @SerializedName("time_ago") val timeAgo: String? = null,
)
