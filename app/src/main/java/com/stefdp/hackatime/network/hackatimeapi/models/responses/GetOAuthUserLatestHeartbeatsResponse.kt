package com.stefdp.hackatime.network.hackatimeapi.models.responses

import com.google.gson.annotations.SerializedName

data class GetOAuthUserLatestHeartbeatsResponse(
    val id: Long,
    val time: Long,
    val category: String,
    @SerializedName("created_at") val createdAt: String,
    val editor: String,
    val entity: String,
    val language: String,
    val machine: String,
    @SerializedName("operating_system") val operatingSystem: String,
    val project: String,
)

data class GetOAuthUserLatestHeartbeatsError(
    val heartbeat: Nothing? = null
)
