package com.stefdp.hackatime.network.backendapi.models.requests

data class SendPushNotificationTokenBody(
    val androidPushToken: String
)
