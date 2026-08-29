package com.stefdp.hackatime.network.hackatimeapi.models

import com.google.gson.annotations.SerializedName

data class TrustFactor(
    @SerializedName("trust_level") val trustLevel: TrustLevel,
    @SerializedName("trust_value") val trustValue: TrustValue
) {
    enum class TrustLevel(val value: String) {
        BLUE("blue"),
        RED("red"),
        GREEN("green");

        override fun toString(): String = value
    }

    enum class TrustValue(val rawValue: Int) {
        @SerializedName("1")
        LOW(1),

        @SerializedName("2")
        MEDIUM(2),

        @SerializedName("3")
        HIGH(3);

        override fun toString(): String = rawValue.toString()
    }
}