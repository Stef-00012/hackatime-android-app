package com.stefdp.hackatime.network.backendapi.models.responses

data class UpdateNotificationCategoriesResponse(
    val success: Boolean,
    val notificationCategories: NotificationCategoriesResponse
)
