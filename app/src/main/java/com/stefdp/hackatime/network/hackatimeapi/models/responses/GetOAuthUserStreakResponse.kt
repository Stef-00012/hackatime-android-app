package com.stefdp.hackatime.network.hackatimeapi.models.responses

import com.google.gson.annotations.SerializedName

data class GetOAuthUserStreakResponse(
    @SerializedName("streak_days") val streakDays: Long,
)
