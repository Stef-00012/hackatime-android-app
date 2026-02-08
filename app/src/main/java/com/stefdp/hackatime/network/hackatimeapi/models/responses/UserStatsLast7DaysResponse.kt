package com.stefdp.hackatime.network.hackatimeapi.models.responses

import com.google.gson.annotations.SerializedName
import com.stefdp.hackatime.network.hackatimeapi.models.CategoryLast7Days
import com.stefdp.hackatime.network.hackatimeapi.models.EditorLast7Days
import com.stefdp.hackatime.network.hackatimeapi.models.LanguageLast7Days
import com.stefdp.hackatime.network.hackatimeapi.models.MachineLast7Days
import com.stefdp.hackatime.network.hackatimeapi.models.OperatingSystemLast7Days
import com.stefdp.hackatime.network.hackatimeapi.models.ProjectLast7Days

data class UserStatsLast7DaysResponse(
    val data: UserStatsLast7Days
)

data class UserStatsLast7Days(
    val username: String?,
    @SerializedName("user_id") val userId: String?,
    val start: String,
    val end: String,
    val status: String,
    @SerializedName("total_seconds") val totalSeconds: Int,
    @SerializedName("daily_average") val dailyAverage: Int,
    @SerializedName("daily_including_holidays") val dailyIncludingHolidays: Int,
    val range: String,
    @SerializedName("human_readable_range") val humanReadableRange: String,
    @SerializedName("human_readable_total") val humanReadableTotal: String,
    @SerializedName("human_readable_daily_average") val humanReadableDailyAverage: String,
    @SerializedName("is_coding_activity_visible") val isCodingActivityVisible: Boolean,
    @SerializedName("is_other_usage_visible") val isOtherUsageVisible: Boolean,
    val editors: List<EditorLast7Days>,
    val languages: List<LanguageLast7Days>,
    val machines: List<MachineLast7Days>,
    val projects: List<ProjectLast7Days>,
    @SerializedName("operating_systems") val operatingSystems: List<OperatingSystemLast7Days>,
    val categories: List<CategoryLast7Days>,
)