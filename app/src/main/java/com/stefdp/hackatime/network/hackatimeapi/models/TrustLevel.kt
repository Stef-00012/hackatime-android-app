package com.stefdp.hackatime.network.hackatimeapi.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
enum class TrustLevel(val value: String) : Parcelable {
    @SerializedName("green")
    GREEN("green"),

    @SerializedName("blue")
    BLUE("blue"),

    @SerializedName("red")
    RED("red");

    override fun toString(): String = value
}