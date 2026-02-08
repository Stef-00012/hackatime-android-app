package com.stefdp.hackatime.network.backendapi.models.responses

data class ErrorResponse(
    val error: String,
    val success: Boolean = false
)
