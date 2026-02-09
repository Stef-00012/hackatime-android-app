package com.stefdp.hackatime.network.backendapi.requests

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.stefdp.hackatime.network.ApiClient
import com.stefdp.hackatime.network.backendapi.models.Goal
import com.stefdp.hackatime.network.backendapi.models.GoalDate
import com.stefdp.hackatime.network.backendapi.models.responses.ErrorResponse
import com.stefdp.hackatime.network.backendapi.models.responses.GetUserGoalsResponse
import com.stefdp.hackatime.utils.SecureStorage

private const val TAG = "BackendApi[getUserGoals]"

suspend fun getUserGoals(
    context: Context,
    all: Boolean?,
    date: GoalDate,
    startDate: GoalDate,
    endDate: GoalDate,
): List<Goal> {
    try {
        val secureStore = SecureStorage.getInstance(context)

        val apiKey = secureStore.get("apiKey")

        if (apiKey == null || apiKey.isEmpty()) {
            return emptyList()
        }

        val response = ApiClient.backendApi.getUserGoals(
            apiKey = apiKey,
            all = all,
            date = date,
            startDate = startDate,
            endDate = endDate,
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

            return emptyList()
        }

        if (body is GetUserGoalsResponse) {
            return body.goals
        }

        return emptyList()
    } catch(e: Exception) {
        Log.e(TAG, "Exception occurred: ${e.message}", e)

        return emptyList()
    }
}