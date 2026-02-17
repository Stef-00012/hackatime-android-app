package com.stefdp.hackatime.utils

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

fun formatMs(
    ms: Double,
    abbreviated: Boolean = true,
    limit: Int = 7
): String {
    val duration = ms.toLong()

    if (duration == 0L) return "0s"

    val second = 1.seconds.inWholeMilliseconds // 1000L
    val minute = 1.minutes.inWholeMilliseconds // second * 60
    val hour = 1.hours.inWholeMilliseconds // minute * 60
    val day = 1.days.inWholeMilliseconds // hour * 24
    val month = day * 30
    val year = day * 365

    val years = duration / year
    val months = (duration % year) / month
    val days = (duration % month) / day
    val hours = (duration % day) / hour
    val minutes = (duration % hour) / minute
    val seconds = (duration % minute) / second

    val secondsLongSuffix = if (seconds > 1) "seconds" else "second"
    val minutesLongSuffix = if (minutes > 1) "minutes" else "minute"
    val hoursLongSuffix = if (hours > 1) "hours" else "hour"
    val daysLongSuffix = if (days > 1) "days" else "day"
    val monthsLongSuffix = if (months > 1) "months" else "month"
    val yearsLongSuffix = if (years > 1) "years" else "year"

    val dateSegments = listOfNotNull(
        if (years > 0) "${years}${if (abbreviated) "y" else yearsLongSuffix}" else null,
        if (months > 0) "${months}${if (abbreviated) "mo" else monthsLongSuffix}" else null,
        if (days > 0) "${days}${if (abbreviated) "d" else daysLongSuffix}" else null,
        if (hours > 0) "${hours}${if (abbreviated) "h" else hoursLongSuffix}" else null,
        if (minutes > 0) "${minutes}${if (abbreviated) "m" else minutesLongSuffix}" else null,
        if (seconds > 0) "${seconds}${if (abbreviated) "s" else secondsLongSuffix}" else null
    )

    return dateSegments.take(limit).joinToString(" ")
}

private val yearUnit = listOf("years", "year", "yr", "y")
private val monthUnit = listOf("months", "month", "mo")
private val dayUnit = listOf("days", "day", "d")
private val hourUnit = listOf("hours", "hour", "hr", "h")
private val minuteUnit = listOf("minutes", "minute", "mins", "min", "m")
private val secondUnit = listOf("seconds", "second", "secs", "sec", "s")

private val combinedUnits = (yearUnit + monthUnit + dayUnit + hourUnit + minuteUnit + secondUnit).joinToString("|")
private val timeRegex = Regex("(\\d+)\\s*($combinedUnits)", RegexOption.IGNORE_CASE)

fun parseTimeToMillis(input: String): Long? {
    if (input.isBlank()) return null

    val matches = timeRegex.findAll(input.lowercase())

    if (!matches.any()) return null

    var totalMillis = 0L

    for (match in matches) {
        val value = match.groupValues[1].toLong()
        val unit = match.groupValues[2]

        totalMillis += when {
            unit in yearUnit -> value * 31_536_000_000L // 365 * 24 * 60 * 60 * 1000
            unit in monthUnit -> value * 2_592_000_000L // 30 * 24 * 60 * 60 * 1000
            unit in dayUnit -> value * 1.days.inWholeMilliseconds // 86_400_000L // 24 * 60 * 60 * 1000
            unit in hourUnit -> value * 1.hours.inWholeMilliseconds // 3_600_000L // 60 * 60 * 1000
            unit in minuteUnit -> value * 1.minutes.inWholeMilliseconds // 60_000L // 60 * 1000
            unit in secondUnit -> value * 1.seconds.inWholeMilliseconds // 1_000L
            else -> 0L
        }
    }

    if (totalMillis < 0) return null

    return totalMillis
}

fun formatGoalDate(date: String): String = Instant
    .parse(date)
    .atZone(ZoneOffset.UTC)
    .format(DateTimeFormatter.ISO_LOCAL_DATE)