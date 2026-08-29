package com.stefdp.hackatime.network.hackatimeapi.models.responses

import com.google.gson.annotations.SerializedName

data class GetLeaderboardResponse(
    @SerializedName("date_range") val dateRange: String,
    val entries: List<Entry>,
    @SerializedName("generated_at") val generatedAt: String,
    val period: String,
    @SerializedName("start_date") val startDate: String,
) {
    data class Entry(
        val rank: Long,
        @SerializedName("total_seconds") val totalSeconds: Long,
        val user: User
    ) {
        data class User(
            @SerializedName("avatar_url") val avatarUrl: String,
            val id: String,
            val username: String,
        )
    }
}
