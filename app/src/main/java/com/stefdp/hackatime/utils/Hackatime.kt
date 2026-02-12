package com.stefdp.hackatime.utils

interface HasTotalSeconds {
    val totalSeconds: Int
}

fun <T : HasTotalSeconds> getTop(
    list: List<T>?
): T? {
    if (list == null || list.isEmpty()) return null

    return list.reduce { prev, curr ->
        if (prev.totalSeconds > curr.totalSeconds) prev else curr
    }
}