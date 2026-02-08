package com.stefdp.hackatime.network.backendapi.requests

import android.util.Log
import com.google.gson.Gson
import com.stefdp.hackatime.network.ApiClient
import com.stefdp.hackatime.network.backendapi.models.NotificationCategory
import com.stefdp.hackatime.network.backendapi.models.responses.ErrorResponse
import com.stefdp.hackatime.network.backendapi.models.responses.NotificationCategoriesResponse

private const val apiKey = "TEMP"

suspend fun getUserNotificationCategories(): Map<NotificationCategory, Boolean> {
    try {
        val response = ApiClient.backendApi.getUserNotificationCategories(
            apiKey = apiKey,
        )

        val body = response.body()

        if (!response.isSuccessful) {
            val statusCode = response.code()

            Log.e("BackendApi[getUserNotificationCategories]", "Request failed with code: $statusCode and message: ${response.message()}")

            return mapOf(
                NotificationCategory.MOTIVATIONAL_QUOTES to false,
                NotificationCategory.GOALS to false,
            )
        }

        if (body is NotificationCategoriesResponse) {
            return mapOf(
                NotificationCategory.MOTIVATIONAL_QUOTES to body.motivationalQuotes,
                NotificationCategory.GOALS to body.goals,
            )
        }

        return mapOf(
            NotificationCategory.MOTIVATIONAL_QUOTES to false,
            NotificationCategory.GOALS to false,
        )
    } catch(e: Exception) {
        Log.e("BackendApi[getUserNotificationCategories]", "Exception occurred: ${e.message}", e)

        return mapOf(
            NotificationCategory.MOTIVATIONAL_QUOTES to false,
            NotificationCategory.GOALS to false,
        )
    }
}