package com.stefdp.hackatime.utils

fun formatMs(
    ms: Double,
    abbreviated: Boolean = true,
    limit: Int = 7
): String {
    val duration = ms.toLong()

    val second = 1000L
    val minute = second * 60
    val hour = minute * 60
    val day = hour * 24
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

//    return buildString {
//        if (years > 0) append("${years}${if (abbreviated) "y" else yearsLongSuffix} ")
//        if (months > 0) append("${months}${if (abbreviated) "mo" else monthsLongSuffix} ")
//        if (days > 0) append("${days}${if (abbreviated) "d" else daysLongSuffix} ")
//        if (hours > 0) append("${hours}${if (abbreviated) "h" else hoursLongSuffix} ")
//        if (minutes > 0) append("${minutes}${if (abbreviated) "m" else minutesLongSuffix} ")
//        if (seconds > 0) append("${seconds}${if (abbreviated) "s" else secondsLongSuffix}")
//    }.trim()
}