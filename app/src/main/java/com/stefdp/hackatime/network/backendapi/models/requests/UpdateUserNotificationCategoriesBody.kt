package com.stefdp.hackatime.network.backendapi.models.requests

import com.stefdp.hackatime.network.backendapi.models.NotificationCategory

data class UpdateUserNotificationCategoriesBody(
    val categories: Map<NotificationCategory, Boolean>
)
