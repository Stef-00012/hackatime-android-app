package com.stefdp.hackatime.network.backendapi.requests

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.stefdp.hackatime.network.ApiClient
import com.stefdp.hackatime.network.backendapi.models.requests.UpdateUserBody
import com.stefdp.hackatime.network.backendapi.models.responses.ErrorResponse
import com.stefdp.hackatime.network.backendapi.models.responses.UpdateUserResponse
import com.stefdp.hackatime.network.hackatimeapi.requests.getOAuthUserApiKeys
import com.stefdp.hackatime.utils.SecureStorage
import kotlinx.datetime.TimeZone

private const val TAG = "BackendApi[updateUser]"

suspend fun updateUser(
    context: Context,
): Boolean {
    try {
        val secureStore = SecureStorage.getInstance(context)

        var apiKey = secureStore.get(SecureStorage.STORAGE_API_KEY)

        if (apiKey.isNullOrEmpty()) {
            val apiKeysResponse = getOAuthUserApiKeys(context)

            if (apiKeysResponse.isFailure) {
                return false
            }

            secureStore.set(SecureStorage.STORAGE_API_KEY, apiKeysResponse.getOrNull()?.token ?: "")

            apiKey = apiKeysResponse.getOrNull()?.token ?: return false
        }

        val timeZoneId = TimeZone.currentSystemDefault().id

        val response = ApiClient.backendApi.updateUser(
            apiKey = apiKey,
            userData = UpdateUserBody(
                timeZone = timeZoneId
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

        if (body is UpdateUserResponse) {
            return body.success
        }

        return false
    } catch(e: Exception) {
        Log.e(TAG, "Exception occurred: ${e.message}", e)

        return false
    }
}