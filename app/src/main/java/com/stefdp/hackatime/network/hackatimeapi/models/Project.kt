package com.stefdp.hackatime.network.hackatimeapi.models

import com.google.gson.annotations.SerializedName
import com.stefdp.hackatime.utils.HasTotalSeconds

data class Project(
    val name: String,
    @SerializedName("total_seconds") override val totalSeconds: Int,
    val text: String,
    val hours: Int,
    val minutes: Int,
    val percent: Double,
    val digital: String,
) : HasTotalSeconds
