package com.stefdp.hackatime.network.hackatimeapi.models.responses

import com.google.gson.annotations.SerializedName

data class GetWakatimeUserTodayDataResponse(
    @SerializedName("grand_total") val grandTotal: GrandTotal,
    val goal: Goal? = null
) {
    data class GrandTotal(
        @SerializedName("total_seconds") val totalSeconds: Long,
        val text: String
    )

    data class Goal(
        @SerializedName("target_seconds") val targetSeconds: Long,
        @SerializedName("tracked_seconds") val trackedSeconds: Long,
        @SerializedName("completion_percent") val completionPercent: Double,
        val complete: Boolean
    )
}