package com.stefdp.hackatime.network.hackatimeapi.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.stefdp.hackatime.utils.GeneralStat
import kotlinx.parcelize.Parcelize

@Parcelize
data class LanguageLast7Days(
    override val name: String,
    @SerializedName("total_seconds") override val totalSeconds: Double,
    override val text: String,
    override val hours: Int,
    override val minutes: Int,
    override val percent: Double,
    override val digital: String,
    val seconds: Int,
) : GeneralStat, Parcelable