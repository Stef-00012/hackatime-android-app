package com.stefdp.hackatime.utils

import com.google.gson.Gson
import com.google.gson.JsonElement

inline fun <reified T> Gson.fromJsonOrNull(json: JsonElement?): T? {
    return runCatching {
        fromJson(json, T::class.java)
    }.getOrNull()
}