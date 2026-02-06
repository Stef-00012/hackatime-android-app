package com.stefdp.hackatime.network.models

import com.google.gson.annotations.SerializedName

enum class TrustLevel {
    @SerializedName("green")
    GREEN,

    @SerializedName("blue")
    BLUE,

    @SerializedName("red")
    RED
}