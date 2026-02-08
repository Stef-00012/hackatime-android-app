package com.stefdp.hackatime.network.backendapi.requests

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.stefdp.hackatime.network.ApiClient
import com.stefdp.hackatime.network.backendapi.models.responses.SendPushNotificationTokenResponse

private const val apiKey = "TEMP"

suspend fun sendPushNotificationToken(
    token: String
): Boolean {
    try {
        val response = ApiClient.backendApi.sendPushNotificationToken(
            apiKey = apiKey,
            token = token
        )

        val body = response.body()

        if (!response.isSuccessful) {
            val statusCode = response.code()

            Log.e("BackendApi[sendPushNotificationToken]", "Request failed with code: $statusCode and message: ${response.message()}")

            return false
        }

        if (body is SendPushNotificationTokenResponse) {
            return body.success
        }

        return false
    } catch(e: Exception) {
        Log.e("BackendApi[sendPushNotificationToken]", "Exception occurred: ${e.message}", e)

        return false
    }
}