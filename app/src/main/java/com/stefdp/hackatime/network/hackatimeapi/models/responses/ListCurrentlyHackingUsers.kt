package com.stefdp.hackatime.network.hackatimeapi.models.responses

import com.google.gson.annotations.SerializedName

data class ListCurrentlyHackingUsers(
    val count: Long,
    val users: List<User>
) {
    data class User(
        @SerializedName("avatar_url") val avatarUrl: String? = null,
        @SerializedName("country_code") val countryCode: String? = null,
        @SerializedName("display_name") val displayName: String? = null,
        @SerializedName("working_on") val workingOn: WorkingOn? = null,
    ) {
        data class WorkingOn(
            @SerializedName("project_name") val projectName: String,
            @SerializedName("repo_url") val repoUrl: String? = null,
        )
    }
}
