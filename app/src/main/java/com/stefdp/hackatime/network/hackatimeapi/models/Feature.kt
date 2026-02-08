package com.stefdp.hackatime.network.hackatimeapi.models

import com.google.gson.annotations.SerializedName

enum class Feature(val value: String) {
    @SerializedName("languages")
    LANGUAGES("languages"),

    @SerializedName("projects")
    PROJECTS("projects"),

    @SerializedName("editors")
    EDITORS("editors"),

    @SerializedName("machines")
    MACHINES("machines"),

    @SerializedName("operating_systems")
    OPERATING_SYSTEMS("operating_systems"),

    @SerializedName("categories")
    CATEGORIES("categories");

    override fun toString(): String = value
}