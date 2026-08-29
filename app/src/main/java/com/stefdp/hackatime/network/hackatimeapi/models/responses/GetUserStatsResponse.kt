package com.stefdp.hackatime.network.hackatimeapi.models.responses

import com.google.gson.annotations.SerializedName
import com.stefdp.hackatime.network.hackatimeapi.models.TrustFactor
import com.stefdp.hackatime.utils.GeneralStat

data class GetUserStatsResponse(
    val data: Data,
    @SerializedName("trust_factor") val trustFactor: TrustFactor,
) {
    data class Data(
        val username: String? = null,
        val userId: String,
        @SerializedName("is_coding_activity_visible") val isCodingActivityVisible: Boolean,
        @SerializedName("is_other_usage_visible") val isOtherUsageVisible: Boolean,
        val status: String? = null,
        val start: String,
        val end: String,
        val range: String,
        @SerializedName("human_readable_range") val humanReadableRange: String,
        @SerializedName("total_seconds") val totalSeconds: Long,
        @SerializedName("daily_average") val dailyAverage: Double,
        @SerializedName("human_readable_daily_average") val humanReadableDailyAverage: String,
        val languages: List<Language> = emptyList(),
        val streak: Long,
        val projects: List<Project> = emptyList(),
        @SerializedName("unique_total_seconds") val uniqueTotalSeconds: Long? = null,
    ) {
        data class Language(
            override val name: String,
            @SerializedName("total_seconds") override val totalSeconds: Long,
            override val text: String,
            override val hours: Long,
            override val minutes: Long,
            override val seconds: Long = 0L,
            override val percent: Double,
            override val digital: String,
            val color: String
        ) : GeneralStat

        data class Project(
            override val name: String,
            @SerializedName("total_seconds") override val totalSeconds: Long,
            override val text: String,
            override val hours: Long,
            override val minutes: Long,
            override val seconds: Long = 0L,
            override val percent: Double,
            override val digital: String,
        ) : GeneralStat
    }
}
