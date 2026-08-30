package com.stefdp.hackatime.screens.settings

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.hackatime.Logger
import com.stefdp.hackatime.R
import com.stefdp.hackatime.network.backendapi.models.NotificationCategory
import com.stefdp.hackatime.network.backendapi.requests.deleteUser
import com.stefdp.hackatime.network.backendapi.requests.getUser
import com.stefdp.hackatime.network.backendapi.requests.getUserNotificationCategories
import com.stefdp.hackatime.network.backendapi.requests.sendApiKey
import com.stefdp.hackatime.network.backendapi.requests.updateUser
import com.stefdp.hackatime.network.backendapi.requests.updateUserNotificationCategories
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetWakatimeUserResponse
import com.stefdp.hackatime.network.hackatimeoauth.requests.revokeOAuthToken
import com.stefdp.hackatime.screens.LoginScreen
import com.stefdp.hackatime.utils.SecureStorage
import com.stefdp.hackatime.utils.getBiometricStatus
import com.stefdp.hackatime.utils.hasNotificationsPermission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isLoading: Boolean = false,
    val biometricAuthenticationStatus: Int = -1,
    val hasNotificationsPermissions: Boolean = false,
    val shareApikey: Boolean = false,
    val unlockWithBiometrics: Boolean = false,
    val isApiOnServer: Boolean = false,
    val motivationalNotificationsEnabled: Boolean = false,
    val goalsNotificationsEnabled: Boolean = false,
)

private const val TAG = "SettingsViewModel"

class SettingsViewModel : ViewModel() {
    private val _state: MutableStateFlow<SettingsUiState> = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    fun init(
        context: Context
    ) {
        viewModelScope.launch {
            val secureStore = SecureStorage.getInstance(context)

            val shareApiKey = secureStore.get(SecureStorage.STORAGE_SHARE_API_KEY)?.toBoolean() ?: false
            val unlockWithBiometrics = secureStore.get(SecureStorage.STORAGE_UNLOCK_WITH_BIOMETRICS)?.toBoolean() ?: false

            val isAPiOnServer = getUser(
                context = context
            )

            val notificationCategories = getUserNotificationCategories(context)

            Logger.debug(TAG, "Notification categories: $notificationCategories, shareApiKey: $shareApiKey, unlockWithBiometrics: $unlockWithBiometrics, isAPiOnServer: $isAPiOnServer")

            _state.update {
                it.copy(
                    biometricAuthenticationStatus = getBiometricStatus(context),
                    hasNotificationsPermissions = hasNotificationsPermission(context),
                    shareApikey = shareApiKey,
                    unlockWithBiometrics = unlockWithBiometrics,
                    isApiOnServer = isAPiOnServer,
                    motivationalNotificationsEnabled = notificationCategories[NotificationCategory.MOTIVATIONAL_QUOTES] == true,
                    goalsNotificationsEnabled = notificationCategories[NotificationCategory.GOALS] == true
                )
            }
        }
    }

    fun setBiometricAuthenticationStatus(status: Int) {
        _state.update {
            it.copy(
                biometricAuthenticationStatus = status
            )
        }
    }

    fun setHasNotificationsPermissions(hasPermissions: Boolean) {
        _state.update {
            it.copy(
                hasNotificationsPermissions = hasPermissions
            )
        }
    }

    fun setShareApiKey(share: Boolean) {
        _state.update {
            it.copy(
                shareApikey = share
            )
        }
    }

    fun setUnlockWithBiometrics(unlock: Boolean) {
        _state.update {
            it.copy(
                unlockWithBiometrics = unlock
            )
        }
    }

    fun saveSettings(
        context: Context,
        updateUserStats: suspend (context: Context) -> Result<GetWakatimeUserResponse.Data>,
        onUserError: () -> Unit,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val secureStore = SecureStorage.getInstance(context)

            secureStore.set(SecureStorage.STORAGE_SHARE_API_KEY, _state.value.shareApikey.toString())

            val newUserRes = updateUserStats(context)

            if (newUserRes.isFailure) {
                _state.update {
                    it.copy(
                        isLoading = false
                    )
                }

                onUserError()

                return@launch
            }

            if (!_state.value.shareApikey && _state.value.isApiOnServer) {
                val res = deleteUser(context)

                if (!res) {
                    secureStore.set(SecureStorage.STORAGE_SHARE_API_KEY, "true")

                    onError(context.getString(R.string.delete_data_fail_message))
                }
            } else if (_state.value.shareApikey && !_state.value.isApiOnServer) {
                val res = sendApiKey(
                    context = context
                )

                if (!res) {
                    secureStore.set(SecureStorage.STORAGE_SHARE_API_KEY, "false")

                    onError(context.getString(R.string.send_data_fail_message))
                }
            }

            _state.update {
                it.copy(
                    isLoading = false
                )
            }

            onSuccess()

            Toast.makeText(
                context,
                context.getString(R.string.settings_saved_message),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun setMotivationalNotificationsEnabled(enabled: Boolean) {
        _state.update {
            it.copy(
                motivationalNotificationsEnabled = enabled
            )
        }
    }

    fun setGoalsNotificationsEnabled(enabled: Boolean) {
        _state.update {
            it.copy(
                goalsNotificationsEnabled = enabled
            )
        }
    }

    fun saveNotificationPreferences(
        context: Context,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val newNotificationCategories = updateUserNotificationCategories(
                context = context,
                categories = mapOf(
                    NotificationCategory.MOTIVATIONAL_QUOTES to _state.value.motivationalNotificationsEnabled,
                    NotificationCategory.GOALS to _state.value.goalsNotificationsEnabled
                )
            )

            updateUser(context)

            _state.update {
                it.copy(
                    isLoading = false,
                    motivationalNotificationsEnabled = newNotificationCategories[NotificationCategory.MOTIVATIONAL_QUOTES] == true,
                    goalsNotificationsEnabled = newNotificationCategories[NotificationCategory.GOALS] == true
                )
            }

            onSuccess()
        }
    }

    fun logout(
        context: Context,
        onLogout: () -> Unit,
        onError: (String) -> Unit,
        updateUserStats: suspend (context: Context) -> Result<GetWakatimeUserResponse.Data>,
    ) {
        viewModelScope.launch {
            val revokeStatus = revokeOAuthToken(context)

            if (revokeStatus.isFailure || revokeStatus.getOrNull() == false) {
                onError(context.getString(R.string.logout_revoke_fail))

                return@launch
            }

            val secureStore = SecureStorage.getInstance(context)

            secureStore.del(SecureStorage.STORAGE_ACCESS_TOKEN)
            secureStore.del(SecureStorage.STORAGE_API_KEY)

            updateUserStats(context)

            onLogout()
        }
    }
}