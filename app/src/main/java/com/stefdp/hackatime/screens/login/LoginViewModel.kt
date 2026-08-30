package com.stefdp.hackatime.screens.login

import android.R.attr.apiKey
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.hackatime.R
import com.stefdp.hackatime.network.backendapi.requests.sendApiKey
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetWakatimeUserResponse
import com.stefdp.hackatime.network.hackatimeapi.requests.getOAuthUserApiKeys
import com.stefdp.hackatime.utils.SecureStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val shareApiKey: Boolean = false,
)

private const val TAG = "LoginViewModel"

class LoginViewModel : ViewModel() {
    private val _state: MutableStateFlow<LoginUiState> = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun setShareApiKey(shareApiKey: Boolean) {
        _state.update {
            it.copy(
                shareApiKey = shareApiKey
            )
        }
    }

    fun updateAccessToken(
        context: Context,
        accessToken: String?,
        updateUserStats: suspend (context: Context) -> Result<GetWakatimeUserResponse.Data>,
        onError: (String) -> Unit
    ) {
        if (accessToken.isNullOrBlank()) return

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val secureStore = SecureStorage.getInstance(context)

            secureStore.set(SecureStorage.STORAGE_ACCESS_TOKEN, accessToken)

            val tokens = getOAuthUserApiKeys(context)

            if (tokens.isSuccess) {
                val apiKey = tokens.getOrNull()?.token

                if (!apiKey.isNullOrBlank()) {
                    secureStore.set(SecureStorage.STORAGE_API_KEY, apiKey)
                }
            }

            if (_state.value.shareApiKey) {
                shareApiKeyWithServer(
                    context = context,
                    onError = onError
                )
            }

            updateUserStats(context)

            _state.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    fun setIsLoading(isLoading: Boolean) {
        _state.update {
            it.copy(
                isLoading = isLoading
            )
        }
    }

    private suspend fun shareApiKeyWithServer(
        context: Context,
        onError: (String) -> Unit
    ) {
        val secureStore = SecureStorage.getInstance(context)

        val res = sendApiKey(
            context = context
        )

        if (!res) {
            secureStore.set(SecureStorage.STORAGE_SHARE_API_KEY, "false")

            onError(context.getString(R.string.send_data_fail_message))
        }
    }
}