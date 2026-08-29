package com.stefdp.hackatime.network.hackatimeapi.models

import com.google.gson.annotations.SerializedName

enum class Feature(val value: String) {
    @SerializedName("languages")
    LANGUAGES("languages"),

    @SerializedName("projects")
    PROJECTS("projects");

    override fun toString(): String = value
}