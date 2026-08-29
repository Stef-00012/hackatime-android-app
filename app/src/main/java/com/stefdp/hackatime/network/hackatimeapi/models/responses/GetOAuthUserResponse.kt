package com.stefdp.hackatime.network.hackatimeapi.models.responses

import com.google.gson.annotations.SerializedName
import com.stefdp.hackatime.network.hackatimeapi.models.TrustFactor

data class GetOAuthUserResponse(
    val emails: List<String>,
    @SerializedName("github_username") val githubUsername: String?,
    val id: Long,
    @SerializedName("slack_id") val slackId: String?,
    @SerializedName("trust_factor") val trustFactor: TrustFactor
)
