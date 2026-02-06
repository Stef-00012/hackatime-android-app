package com.stefdp.hackatime.network.models.responses

import com.google.gson.annotations.SerializedName

data class UserStatsTotalSecondsResponse(
    @SerializedName("total_seconds") val totalSeconds: Int
)
