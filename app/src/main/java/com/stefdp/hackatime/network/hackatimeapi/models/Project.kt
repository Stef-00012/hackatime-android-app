package com.stefdp.hackatime.network.hackatimeapi.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.stefdp.hackatime.utils.GeneralStat
import kotlinx.parcelize.Parcelize

@Parcelize
data class Project(
    override val name: String,
    @SerializedName("total_seconds") override val totalSeconds: Int,
    override val text: String,
    override val hours: Int,
    override val minutes: Int,
    override val percent: Double,
    override val digital: String,
) : GeneralStat, Parcelable
