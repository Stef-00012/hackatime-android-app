package com.stefdp.hackatime.utils

import android.content.Context
import com.stefdp.hackatime.network.hackatimeapi.models.Feature
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetUserStatsResponse
import com.stefdp.hackatime.network.hackatimeapi.requests.getUserStats
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days

interface GeneralStat {
    val name: String
    val totalSeconds: Long
    val text: String
    val hours: Long
    val minutes: Long
    val seconds: Long
    val percent: Double
    val digital: String
}

fun <T : GeneralStat> getTop(
    list: List<T>?
): T? {
    if (list.isNullOrEmpty()) return null

    return list.maxByOrNull { it.totalSeconds }
}

data class DayData(
    var date: String,
    var data: GetUserStatsResponse.Data? = null
)

suspend fun getLast7DaysData(
    context: Context
): List<DayData> {
    val last7DaysData = mutableListOf<DayData>()

    val today = Clock.System.now().toEpochMilliseconds()
    val oneDayMillis = 1.days.inWholeMilliseconds //24 * 60 * 60 * 1000L

    for (i in 0..6) {
        val startDate = if (i == 0) today else (today - i * oneDayMillis)
        val startDateString = Instant
            .ofEpochMilli(startDate)
            .atZone(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_LOCAL_DATE)

        val endDate = startDate + oneDayMillis
        val endDateString = Instant
            .ofEpochMilli(endDate)
            .atZone(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_LOCAL_DATE)

        val userStats = getUserStats(
            context = context,
            username = "my",
            startDate = startDateString,
            endDate = endDateString,
            features = listOf(
                Feature.PROJECTS,
                Feature.LANGUAGES
            )
        )

        last7DaysData.add(
            DayData(
                date = startDateString,
                data = userStats.getOrNull()?.data
            )
        )
    }

    return last7DaysData.toList().reversed()
}