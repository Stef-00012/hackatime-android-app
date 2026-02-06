package com.stefdp.hackatime.network.models.responses

import com.google.gson.annotations.SerializedName

data class UserTodayDataResponse(
    @SerializedName("grand_total") val grandTotal: GrandTotal
)

data class GrandTotal(
    val text: String,
    @SerializedName("total_seconds") val totalSeconds: Double
)