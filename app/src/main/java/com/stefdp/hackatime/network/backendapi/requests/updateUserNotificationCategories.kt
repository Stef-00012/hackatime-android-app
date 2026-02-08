package com.stefdp.hackatime.network.backendapi.requests

import android.util.Log
import com.stefdp.hackatime.network.ApiClient
import com.stefdp.hackatime.network.backendapi.models.NotificationCategory
import com.stefdp.hackatime.network.backendapi.models.responses.NotificationCategoriesResponse

private const val apiKey = "TEMP"

suspend fun updateUserNotificationCategories(
    categories: Map<NotificationCategory, Boolean>
): Map<NotificationCategory, Boolean> {
    try {
        val response = ApiClient.backendApi.updateUserNotificationCategories(
            apiKey = apiKey,
            categories = categories
        )

        val body = response.body()

        if (!response.isSuccessful) {
            val statusCode = response.code()

            Log.e("BackendApi[updateUserNotificationCategories]", "Request failed with code: $statusCode and message: ${response.message()}")

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
        Log.e("BackendApi[updateUserNotificationCategories]", "Exception occurred: ${e.message}", e)

        return mapOf(
            NotificationCategory.MOTIVATIONAL_QUOTES to false,
            NotificationCategory.GOALS to false,
        )
    }
}