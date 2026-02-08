package com.stefdp.hackatime.network.hackatimeapi.requests

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.stefdp.hackatime.network.ApiClient
import com.stefdp.hackatime.network.hackatimeapi.models.responses.ErrorResponse
import com.stefdp.hackatime.utils.SecureStorage

suspend fun getYSWSPrograms(context: Context): Result<List<String>> {
    var secureStore = SecureStorage.getInstance(context)

    Log.d("HackatimeApi[getYSWSPrograms]", secureStore.get("apiKey") ?: "No API Key found")

    try {
        val response = ApiClient.hackatimeApi.getYSWSPrograms()

        val body = response.body()

        if (!response.isSuccessful) {
            val statusCode = response.code()

            Log.e("HackatimeApi[getYSWSPrograms]", "Request failed with code: $statusCode and message: ${response.message()}")

            if (statusCode == 401) {
                return Result.failure(
                    Exception("Invalid API Key")
                )
            }

            val errorBody = response.errorBody()?.string()
            val json = Gson().fromJson(errorBody, ErrorResponse::class.java)

            if (json.error.isNotEmpty()) {
                return Result.failure(
                    Exception(json.error)
                )
            }

            return Result.failure(
                Exception("Something went wrong...")
            )
        }

        if (body is List<String>) {
            return Result.success(body)
        }

        return Result.failure(
            Exception("Something went wrong...")
        )
    } catch(e: Exception) {
        Log.e("HackatimeApi[getYSWSPrograms]", "Exception occurred: ${e.message}", e)

        return Result.failure(
            Exception("Something went wrong...")
        )
    }
}