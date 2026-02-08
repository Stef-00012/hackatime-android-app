package com.stefdp.hackatime.network.backendapi.requests

import android.util.Log
import com.stefdp.hackatime.network.ApiClient
import com.stefdp.hackatime.network.backendapi.models.Goal
import com.stefdp.hackatime.network.backendapi.models.GoalDate
import com.stefdp.hackatime.network.backendapi.models.responses.GetUserGoalsResponse

private const val apiKey = "TEMP"

suspend fun getUserGoals(
    all: Boolean?,
    date: GoalDate,
    startDate: GoalDate,
    endDate: GoalDate,
): List<Goal> {
    try {
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

            Log.e("BackendApi[getUserGoals]", "Request failed with code: $statusCode and message: ${response.message()}")

            return emptyList()
        }

        if (body is GetUserGoalsResponse) {
            return body.goals
        }

        return emptyList()
    } catch(e: Exception) {
        Log.e("BackendApi[getUserGoals]", "Exception occurred: ${e.message}", e)

        return emptyList()
    }
}