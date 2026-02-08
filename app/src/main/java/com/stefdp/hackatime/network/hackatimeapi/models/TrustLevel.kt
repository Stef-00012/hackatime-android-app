package com.stefdp.hackatime.network.hackatimeapi.models

import com.google.gson.annotations.SerializedName

enum class TrustLevel(val value: String) {
    @SerializedName("green")
    GREEN("green"),

    @SerializedName("blue")
    BLUE("blue"),

    @SerializedName("red")
    RED("red");

    override fun toString(): String = value
}