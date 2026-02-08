package com.stefdp.hackatime.network.hackatimeapi.models

import com.google.gson.annotations.SerializedName

data class UserTrustFactor(
    @SerializedName("trust_level") val trustLevel: TrustLevel,
    @SerializedName("trust_value") val trustValue: Int
)
