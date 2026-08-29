package com.stefdp.hackatime.network.backendapi.requests

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.stefdp.hackatime.network.ApiClient
import com.stefdp.hackatime.network.backendapi.models.NotificationCategory
import com.stefdp.hackatime.network.backendapi.models.responses.ErrorResponse
import com.stefdp.hackatime.network.backendapi.models.responses.UpdateNotificationCategoriesResponse
import com.stefdp.hackatime.network.hackatimeapi.requests.getOAuthUserApiKeys
import com.stefdp.hackatime.utils.SecureStorage

private const val TAG = "BackendApi[updateUserNotificationCategories]"

suspend fun updateUserNotificationCategories(
    context: Context,
    categories: Map<NotificationCategory, Boolean>
): Map<NotificationCategory, Boolean> {
    try {
        val secureStore = SecureStorage.getInstance(context)

        var apiKey = secureStore.get(SecureStorage.STORAGE_API_KEY)

        if (apiKey.isNullOrEmpty()) {
            val apiKeysResponse = getOAuthUserApiKeys(context)

            if (apiKeysResponse.isFailure) {
                return mapOf(
                    NotificationCategory.MOTIVATIONAL_QUOTES to false,
                    NotificationCategory.GOALS to false,
                )
            }

            secureStore.set(SecureStorage.STORAGE_API_KEY, apiKeysResponse.getOrNull()?.token ?: "")

            apiKey = apiKeysResponse.getOrNull()?.token ?: return mapOf(
                NotificationCategory.MOTIVATIONAL_QUOTES to false,
                NotificationCategory.GOALS to false,
            )
        }

        val response = ApiClient.backendApi.updateUserNotificationCategories(
            apiKey = apiKey,
            categories = categories
        )

        val body = response.body()

        if (!response.isSuccessful) {
            val statusCode = response.code()

            Log.e(TAG, "Request failed with code: $statusCode and message: ${response.message()}")

            val errorBody = response.errorBody()?.string()
            val json = Gson().fromJson(errorBody, ErrorResponse::class.java)

            if (json.error.isNotEmpty()) {
                Log.e(TAG, "Error message: ${json.error}")
            }

            return mapOf(
                NotificationCategory.MOTIVATIONAL_QUOTES to false,
                NotificationCategory.GOALS to false,
            )
        }

        if (body is UpdateNotificationCategoriesResponse) {
            return mapOf(
                NotificationCategory.MOTIVATIONAL_QUOTES to body.notificationCategories.motivationalQuotes,
                NotificationCategory.GOALS to body.notificationCategories.goals,
            )
        }

        return mapOf(
            NotificationCategory.MOTIVATIONAL_QUOTES to false,
            NotificationCategory.GOALS to false,
        )
    } catch(e: Exception) {
        Log.e(TAG, "Exception occurred: ${e.message}", e)

        return mapOf(
            NotificationCategory.MOTIVATIONAL_QUOTES to false,
            NotificationCategory.GOALS to false,
        )
    }
}