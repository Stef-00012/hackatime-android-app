package com.stefdp.hackatime.network.models.responses

import com.google.gson.annotations.SerializedName

data class UserHeartbeatSpansResponse(
    val spans: List<HeartbeatSpan>
)

data class HeartbeatSpan(
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    val duration: Double,
)