package com.stefdp.hackatime.network.hackatimeapi.models.responses

import com.google.gson.annotations.SerializedName
import com.stefdp.hackatime.network.hackatimeapi.models.Heartbeat

data class CurrentUserRawHeartbeatsResponse(
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    @SerializedName("total_seconds") val totalSeconds: Int,
    val heartbeats: List<Heartbeat>
)