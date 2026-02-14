package com.stefdp.hackatime.network.hackatimeapi.models

import com.google.gson.annotations.SerializedName
import com.stefdp.hackatime.utils.GeneralStat

data class OperatingSystemLast7Days(
    override val name: String,
    @SerializedName("total_seconds") override val totalSeconds: Int,
    override val text: String,
    override val hours: Int,
    override val minutes: Int,
    override val percent: Double,
    override val digital: String,
    val seconds: Int,
) : GeneralStat