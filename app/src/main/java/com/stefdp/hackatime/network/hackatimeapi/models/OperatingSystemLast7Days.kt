package com.stefdp.hackatime.network.hackatimeapi.models

import com.google.gson.annotations.SerializedName

data class OperatingSystemLast7Days(
    val name: String,
    @SerializedName("total_seconds") val totalSeconds: Int,
    val text: String,
    val hours: Int,
    val minutes: Int,
    val percent: Double,
    val digital: String,
    val seconds: Int,
)
