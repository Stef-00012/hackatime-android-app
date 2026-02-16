package com.stefdp.hackatime.network.hackatimeapi.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.stefdp.hackatime.utils.GeneralStat
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProjectLast7Days(
    override val name: String,
    @SerializedName("total_seconds") override val totalSeconds: Double,
    override val text: String,
    override val hours: Double,
    override val minutes: Double,
    override val percent: Double,
    override val digital: String,
    val seconds: Double,
) : GeneralStat, Parcelable