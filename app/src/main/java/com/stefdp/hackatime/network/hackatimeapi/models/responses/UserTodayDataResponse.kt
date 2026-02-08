package com.stefdp.hackatime.network.hackatimeapi.models.responses

import com.google.gson.annotations.SerializedName

data class UserTodayDataResponse(
    val data: UserTodayData
)

data class UserTodayData(
    @SerializedName("grand_total") val grandTotal: GrandTotal
)

data class GrandTotal(
    val text: String,
    @SerializedName("total_seconds") val totalSeconds: Double
)