package com.stefdp.hackatime.network.hackatimeapi.models.responses

import com.google.gson.annotations.SerializedName

data class GetUserTotalSecondsResponse(
    @SerializedName("total_seconds") val totalSeconds: Long
)
