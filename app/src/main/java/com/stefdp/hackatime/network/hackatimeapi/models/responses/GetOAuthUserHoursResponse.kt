package com.stefdp.hackatime.network.hackatimeapi.models.responses

import com.google.gson.annotations.SerializedName

data class GetOAuthUserHoursResponse(
    @SerializedName("start_date") val startDate: String,
    @SerializedName("end_date") val endDate: String,
    @SerializedName("total_seconds") val totalSeconds: Long,
)