package com.stefdp.hackatime.network.hackatimeapi.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserTrustFactor(
    @SerializedName("trust_level") val trustLevel: TrustLevel,
    @SerializedName("trust_value") val trustValue: Int
) : Parcelable
