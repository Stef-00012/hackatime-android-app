package com.stefdp.hackatime.network.backendapi.requests

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.stefdp.hackatime.network.ApiClient
import com.stefdp.hackatime.network.backendapi.models.Goal
import com.stefdp.hackatime.network.backendapi.models.GoalDate
import com.stefdp.hackatime.network.backendapi.models.requests.UpdateUserGoalBody
import com.stefdp.hackatime.network.backendapi.models.responses.ErrorResponse
import com.stefdp.hackatime.utils.SecureStorage

private const val TAG = "BackendApi[updateUserGoal]"

suspend fun updateUserGoal(
    context: Context,
    goal: Long,
    date: GoalDate
): Boolean {
    try {
        val secureStore = SecureStorage.getInstance(context)

        val apiKey = secureStore.get("apiKey")

        if (apiKey.isNullOrEmpty()) {
            return false
        }

        val response = ApiClient.backendApi.updateUserGoal(
            apiKey = apiKey,
            goalData = UpdateUserGoalBody(
                goal = goal,
                date = date
            )
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

            return false
        }

        if (body is Goal) {
            return true
        }

        return false
    } catch(e: Exception) {
        Log.e(TAG, "Exception occurred: ${e.message}", e)

        return false
    }
}