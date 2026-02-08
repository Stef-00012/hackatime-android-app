package com.stefdp.hackatime.network.hackatimeapi.models.responses

import com.google.gson.annotations.SerializedName
import com.stefdp.hackatime.network.hackatimeapi.models.Language
import com.stefdp.hackatime.network.hackatimeapi.models.Project
import com.stefdp.hackatime.network.hackatimeapi.models.UserTrustFactor

data class UserStatsResponse(
    val data: UserStats
)

data class UserStats(
    val username: String?,
    @SerializedName("user_id") val userId: String,
    @SerializedName("is_coding_activity_visible") val isCodingActivityVisible: Boolean,
    @SerializedName("is_other_usage_visible") val isOtherUsageVisible: Boolean,
    val status: String,
    val start: String,
    val end: String,
    val range: String,
    @SerializedName("human_readable_range") val humanReadableRange: String,
    @SerializedName("total_seconds") val totalSeconds: Double,
    @SerializedName("daily_average") val dailyAverage: Double,
    @SerializedName("human_readable_total") val humanReadablTotal: String,
    @SerializedName("human_readable_daily_average") val humanReadableDailyAverage: String,
    val languages: List<Language>? = null,
    val projects: List<Project>? = null,
    @SerializedName("trust_factor") val trustFactor: UserTrustFactor
)