package com.stefdp.hackatime.network.backendapi.requests

import android.util.Log
import com.stefdp.hackatime.network.ApiClient
import com.stefdp.hackatime.network.backendapi.models.GoalDate
import com.stefdp.hackatime.network.backendapi.models.responses.UpdateUserGoalResponse

private const val apiKey = "TEMP"

suspend fun updateUserGoal(
    goal: Int,
    date: GoalDate
): Boolean {
    try {
        val response = ApiClient.backendApi.updateUserGoal(
            apiKey = apiKey,
            goal = goal,
            date = date,
        )

        val body = response.body()

        if (!response.isSuccessful) {
            val statusCode = response.code()

            Log.e("BackendApi[updateUserGoal]", "Request failed with code: $statusCode and message: ${response.message()}")

            return false
        }

        if (body is UpdateUserGoalResponse) {
            return body.success
        }

        return false
    } catch(e: Exception) {
        Log.e("BackendApi[updateUserGoal]", "Exception occurred: ${e.message}", e)

        return false
    }
}