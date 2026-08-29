package com.stefdp.hackatime.network.hackatimeapi.models.responses

import com.google.gson.annotations.SerializedName
import com.stefdp.hackatime.utils.GeneralStat

data class GetWakatimeUserLast7DaysStatsResponse(
    val data: Data
) {
    data class Data(
        val categories: List<Category>,
        @SerializedName("daily_average") val dailyAverage: Double,
        @SerializedName("days_including_holidays") val daysIncludingHolidays: Long,
        val editors: List<Editor>,
        val end: String,
        @SerializedName("human_readable_daily_average") val humanReadableDailyAverage: String,
        @SerializedName("human_readable_range") val humanReadableRange: String,
        @SerializedName("human_readable_total") val humanReadableTotal: String,
        @SerializedName("is_coding_activity_visible") val isCodingActivityVisible: Boolean,
        @SerializedName("is_other_usage_visible") val isOtherUsageVisible: Boolean,
        val languages: List<Language>,
        val machines: List<Machine>,
        @SerializedName("operating_systems") val operatingSystems: List<OperatingSystem>,
        val projects: List<Project>,
        val range: String,
        val start: String,
        val status: String,
        @SerializedName("total_seconds") val totalSeconds: Long,
        @SerializedName("user_id") val userId: String,
        val username: String
    ) {
        data class Category(
            override val digital: String,
            override val hours: Long,
            override val minutes: Long,
            override val name: String,
            override val percent: Double,
            override val seconds: Long,
            override val text: String,
            @SerializedName("total_seconds") override val totalSeconds: Long
        ) : GeneralStat

        data class Editor(
            override val digital: String,
            override val hours: Long,
            override val minutes: Long,
            override val name: String,
            override val percent: Double,
            override val seconds: Long,
            override val text: String,
            @SerializedName("total_seconds") override val totalSeconds: Long
        ) : GeneralStat

        data class Language(
            override val digital: String,
            override val hours: Long,
            override val minutes: Long,
            override val name: String,
            override val percent: Double,
            override val seconds: Long,
            override val text: String,
            @SerializedName("total_seconds") override val totalSeconds: Long
        ) : GeneralStat

        data class Machine(
            override val digital: String,
            override val hours: Long,
            override val minutes: Long,
            override val name: String,
            override val percent: Double,
            override val seconds: Long,
            override val text: String,
            @SerializedName("total_seconds") override val totalSeconds: Long
        ) : GeneralStat

        data class OperatingSystem(
            override val digital: String,
            override val hours: Long,
            override val minutes: Long,
            override val name: String,
            override val percent: Double,
            override val seconds: Long,
            override val text: String,
            @SerializedName("total_seconds") override val totalSeconds: Long
        ) : GeneralStat

        data class Project(
            override val digital: String,
            override val hours: Long,
            override val minutes: Long,
            override val name: String,
            override val percent: Double,
            override val seconds: Long,
            override val text: String,
            @SerializedName("total_seconds") override val totalSeconds: Long
        ) : GeneralStat
    }
}
