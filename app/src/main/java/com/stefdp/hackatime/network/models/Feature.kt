package com.stefdp.hackatime.network.models

import com.google.gson.annotations.SerializedName

enum class Feature {
    @SerializedName("languages")
    LANGUAGES,

    @SerializedName("projects")
    PROJECTS,

    @SerializedName("editors")
    EDITORS,

    @SerializedName("machines")
    MACHINES,

    @SerializedName("operating_systems")
    OPERATING_SYSTEMS,

    @SerializedName("categories")
    CATEGORIES
}