package com.stefdp.hackatime.network.backendapi.models.responses

import com.google.gson.annotations.SerializedName

data class NotificationCategoriesResponse(
    @SerializedName("motivational-quotes") val motivationalQuotes: Boolean,
    @SerializedName("goals") val goals: Boolean
)
