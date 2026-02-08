package com.stefdp.hackatime.network.backendapi.requests

import android.util.Log
import com.stefdp.hackatime.network.ApiClient
import com.stefdp.hackatime.network.backendapi.models.responses.GetUserResponse

private const val apiKey = "TEMP"

suspend fun getUser(): Boolean {
    try {
        val response = ApiClient.backendApi.getUser(
            apiKey = apiKey,
        )

        val body = response.body()

        if (!response.isSuccessful) {
            val statusCode = response.code()

            Log.e("BackendApi[getUser]", "Request failed with code: $statusCode and message: ${response.message()}")

            return false
        }

        if (body is GetUserResponse) {
            return body.success
        }

        return false
    } catch(e: Exception) {
        Log.e("BackendApi[getUser]", "Exception occurred: ${e.message}", e)

        return false
    }
}