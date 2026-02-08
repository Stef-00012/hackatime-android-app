package com.stefdp.hackatime.network.hackatimeapi.models

import com.google.gson.annotations.SerializedName

data class MachineLast7Days(
    val name: String,
    @SerializedName("total_seconds") val totalSeconds: Int,
    val text: String,
    val hours: Int,
    val minutes: Int,
    val percent: Double,
    val digital: String,
    val seconds: Int,
)
