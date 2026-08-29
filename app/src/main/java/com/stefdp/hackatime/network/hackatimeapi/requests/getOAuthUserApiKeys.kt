package com.stefdp.hackatime.network.hackatimeapi.requests

import android.content.Context
import com.google.gson.Gson
import com.stefdp.hackatime.Logger
import com.stefdp.hackatime.R
import com.stefdp.hackatime.network.ApiClient
import com.stefdp.hackatime.network.hackatimeapi.models.responses.ErrorResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetOAuthUserApiKeysResponse
import com.stefdp.hackatime.utils.SecureStorage

private const val TAG = "HackatimeApi[getOAuthUserApiKeys]"

suspend fun getOAuthUserApiKeys(
    context: Context
): Result<GetOAuthUserApiKeysResponse> {
    try {
        val secureStore = SecureStorage.getInstance(context)

        val accessToken = secureStore.get(SecureStorage.STORAGE_ACCESS_TOKEN)

        if (accessToken.isNullOrEmpty()) {
            return Result.failure(
                Exception(context.getString(R.string.missing_access_token))
            )
        }

        val response = ApiClient.hackatimeApi.getOAuthUserApiKeys(
            authorization = "Bearer $accessToken",
        )

        val body = response.body()

        if (!response.isSuccessful) {
            val statusCode = response.code()

            Logger.error(TAG, "Request failed with code: $statusCode and message: ${response.message()}")

            if (statusCode == 401) {
                return Result.failure(
                    Exception(context.getString(R.string.invalid_access_token))
                )
            }

            val errorBody = response.errorBody()?.string()
            val json = Gson().fromJson(errorBody, ErrorResponse::class.java)

            if (json.error.isNotEmpty()) {
                Logger.error(TAG, "Error message: ${json.error}")

                return Result.failure(
                    Exception(json.error)
                )
            }

            return Result.failure(
                Exception(context.getString(R.string.generic_error))
            )
        }

        if (body is GetOAuthUserApiKeysResponse) {
            return Result.success(body)
        }

        return Result.failure(
            Exception(context.getString(R.string.generic_error))
        )
    } catch(e: Exception) {
        Logger.error(TAG, "Exception occurred: ${e.message}", e)

        return Result.failure(
            Exception(context.getString(R.string.generic_error))
        )
    }
}