package com.stefdp.hackatime.network.hackatimeapi.models.responses

import com.google.gson.annotations.SerializedName

data class ListMyHeartbeatsResponse(
    @SerializedName("end_time") val endTime: String,
    val heartbeats: List<Heartbeat>,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("total_seconds") val totalSeconds: Long
)
