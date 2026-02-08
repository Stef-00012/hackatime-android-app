package com.stefdp.hackatime.network.backendapi.requests

import android.util.Log
import com.stefdp.hackatime.network.ApiClient
import com.stefdp.hackatime.network.backendapi.models.responses.DeleteUserResponse

private const val apiKey = "TEMP"

suspend fun deleteUser(): Boolean {
    try {
        val response = ApiClient.backendApi.deleteUser(
            apiKey = apiKey,
        )

        val body = response.body()

        if (!response.isSuccessful) {
            val statusCode = response.code()

            Log.e("BackendApi[deleteUser]", "Request failed with code: $statusCode and message: ${response.message()}")

            return false
        }

        if (body is DeleteUserResponse) {
            return body.success
        }

        return false
    } catch(e: Exception) {
        Log.e("BackendApi[deleteUser]", "Exception occurred: ${e.message}", e)

        return false
    }
}