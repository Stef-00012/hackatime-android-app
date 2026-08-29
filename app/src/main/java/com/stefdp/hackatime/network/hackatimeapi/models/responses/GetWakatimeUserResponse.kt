package com.stefdp.hackatime.network.hackatimeapi.models.responses

import com.google.gson.annotations.SerializedName

data class GetWakatimeUserResponse(
    val data: Data
) {
    data class Data(
        val id: String,
        val username: String? = null,
        @SerializedName("display_name") val displayName: String,
        @SerializedName("full_name") val fullName: String,
        val photo: String?,
        val timezone: String,
        @SerializedName("created_at") val createdAt: String,
        @SerializedName("modified_at") val modifiedAt: String,
        val plan: String,
    )
}