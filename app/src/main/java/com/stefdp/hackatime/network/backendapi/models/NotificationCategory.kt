package com.stefdp.hackatime.network.backendapi.models

import com.google.gson.annotations.SerializedName

enum class NotificationCategory(val value: String) {
    @SerializedName("motivational-quotes")
    MOTIVATIONAL_QUOTES("motivational-quotes"),

    @SerializedName("goals")
    GOALS("goals");

    override fun toString(): String = value
}
