package com.stefdp.hackatime.utils

import android.content.Context
import com.stefdp.hackatime.network.hackatimeapi.models.Feature
import com.stefdp.hackatime.network.hackatimeapi.models.responses.UserStats
import com.stefdp.hackatime.network.hackatimeapi.requests.getCurrentUserStats
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.time.Clock

interface GeneralStat {
    val name: String
    val totalSeconds: Int
    val text: String
    val hours: Int
    val minutes: Int
    val percent: Double
    val digital: String
}

fun <T : GeneralStat> getTop(
    list: List<T>?
): T? {
    if (list.isNullOrEmpty()) return null

//    return list.reduce { prev, curr ->
//        if (prev.totalSeconds > curr.totalSeconds) prev else curr
//    }

    return list.maxByOrNull { it.totalSeconds }
}

data class DayData(
    var date: String,
    var data: UserStats? = null
)

suspend fun getLast7DaysData(
    context: Context
): List<DayData> {
    val last7DaysData = mutableListOf<DayData>()

    val today = Clock.System.now().toEpochMilliseconds()
    val oneDayMillis = 24 * 60 * 60 * 1000L

    for (i in 0..6) {
        val startDate = if (i == 0) today else (today - i * oneDayMillis)
        val startDateString = startDate.let { millis ->
            val instant = Instant.ofEpochMilli(millis)
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE

            instant.atZone(ZoneOffset.UTC).format(formatter)
        }

        val endDate = startDate + oneDayMillis
        val endDateString = endDate.let { millis ->
            val instant = Instant.ofEpochMilli(millis)
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE

            instant.atZone(ZoneOffset.UTC).format(formatter)
        }

        val userStats = getCurrentUserStats(
            context = context,
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
                data = userStats.getOrNull()
            )
        )
    }

    return last7DaysData.toList().reversed()
}