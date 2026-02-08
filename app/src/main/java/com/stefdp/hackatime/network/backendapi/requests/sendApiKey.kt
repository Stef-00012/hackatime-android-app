package com.stefdp.hackatime.network.backendapi.requests

import android.util.Log
import com.google.gson.Gson
import com.stefdp.hackatime.network.ApiClient
import com.stefdp.hackatime.network.backendapi.models.responses.ErrorResponse
import com.stefdp.hackatime.network.backendapi.models.responses.SendApiKeyResponse

private const val apiKey = "TEMP"

suspend fun sendApiKey(): Boolean {
    try {
        val response = ApiClient.backendApi.sendApiKey(
            apiKey = apiKey,
        )

        val body = response.body()

        if (!response.isSuccessful) {
            val statusCode = response.code()

            Log.e("BackendApi[sendApiKey]", "Request failed with code: $statusCode and message: ${response.message()}")

            return false
        }

        if (body is SendApiKeyResponse) {
            return body.success
        }

        return false
    } catch(e: Exception) {
        Log.e("BackendApi[sendApiKey]", "Exception occurred: ${e.message}", e)

        return false
    }
}