package com.stefdp.hackatime.screens.leaderboard

import android.content.Context
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.annotations.SerializedName
import com.stefdp.hackatime.Logger
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetLeaderboardResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.GetWakatimeUserResponse
import com.stefdp.hackatime.network.hackatimeapi.models.responses.ListCurrentlyHackingUsers
import com.stefdp.hackatime.network.hackatimeapi.requests.getDailyLeaderboard
import com.stefdp.hackatime.network.hackatimeapi.requests.getWeeklyLeaderboard
import com.stefdp.hackatime.network.hackatimeapi.requests.listCurrentlyHackingUsers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Instant

data class LeaderboardUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val entries: List<LeaderboardEntry>? = null,
    val chunkedEntries: List<List<LeaderboardEntry>>? = null,
    val range: LeaderboardRange = LeaderboardRange.DAILY,
    val userCountry: String? = null,
    val type: LeaderboardType = LeaderboardType.GLOBAL,
    val searchQuery: TextFieldValue = TextFieldValue(""),
    val generatedAt: String? = null,
    val dateRange: String? = null,
    val page: Long = 1L,
)

private const val TAG = "LeaderboardViewModel"

class LeaderboardViewModel : ViewModel() {
    private val _state: MutableStateFlow<LeaderboardUiState> = MutableStateFlow(LeaderboardUiState())
    val state: StateFlow<LeaderboardUiState> = _state.asStateFlow()

    fun updateLeaderboard(
        context: Context,
        user: GetWakatimeUserResponse.Data?,
        isRefresh: Boolean
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    isRefreshing = isRefresh,
                    entries = null,
                    chunkedEntries = null,
                )
            }

            val leaderboardRes = when (_state.value.range) {
                LeaderboardRange.DAILY -> {
                    getDailyLeaderboard(context)
                }

                LeaderboardRange.WEEKLY -> {
                    getWeeklyLeaderboard(context)
                }
            }

            if (leaderboardRes.isFailure) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false
                    )
                }

                return@launch
            }

            val data = leaderboardRes.getOrNull()

            if (data == null) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false
                    )
                }

                return@launch
            }

            val currentlyHackingUsersRes = listCurrentlyHackingUsers(context)

            if (currentlyHackingUsersRes.isFailure) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false
                    )
                }

                return@launch
            }

            val hackingUsers = currentlyHackingUsersRes.getOrNull()?.users

            if (hackingUsers == null) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false
                    )
                }

                return@launch
            }

            val self = hackingUsers.find { it.displayName == user?.displayName || it.displayName == user?.username }

            val entries = data.entries
                .map { entry ->
                    val hackingUser = hackingUsers.find { hackingUser -> hackingUser.displayName == entry.user.username }

                    LeaderboardEntry(
                        rank = entry.rank,
                        totalSeconds = entry.totalSeconds,
                        user = hackingUser,
                        leaderboardUser = entry.user,
                        isSelf = entry.user.username == user?.username || entry.user.username == user?.displayName || hackingUser?.displayName == user?.username || hackingUser?.displayName == user?.displayName
                    )
                }

            _state.update {
                it.copy(
                    isLoading = false,
                    isRefreshing = false,
                    entries = entries,
                    userCountry = self?.countryCode,
                    generatedAt = data.generatedAt,
                    dateRange = data.dateRange,
                )
            }

            updateLeaderboardFilters()
        }
    }

    fun updateLeaderboardFilters() {
        _state.update {
            val newChunkedEntries = it.entries
                ?.filter { entry ->
                    entry.user?.displayName
                        ?.lowercase()
                        ?.startsWith(
                            _state.value.searchQuery.text
                                .trim()
                                .lowercase()
                        ) == true ||
                    entry.leaderboardUser.username
                        .lowercase()
                        .startsWith(
                            _state.value.searchQuery.text
                                .trim()
                                .lowercase()
                        )
                }
                ?.filter { entry ->
                    when (_state.value.type) {
                        LeaderboardType.GLOBAL -> true
                        LeaderboardType.COUNTRY -> entry.user?.countryCode == it.userCountry
                    }
                }
                ?.chunked(25)

            it.copy(
                chunkedEntries = newChunkedEntries,
                page = if (
                    !newChunkedEntries.isNullOrEmpty() &&
                    it.page > newChunkedEntries.size
                ) {
                    newChunkedEntries.size.toLong()
                } else {
                    it.page
                }
            )
        }
    }

    fun setLeaderboardRange(type: LeaderboardRange) {
        _state.update {
            it.copy(
                range = type
            )
        }
    }

    fun setLeaderboardType(type: LeaderboardType) {
        _state.update {
            it.copy(
                type = type
            )
        }
    }

    fun setSearchQuery(query: TextFieldValue) {
        _state.update {
            it.copy(
                searchQuery = query
            )
        }
    }

    fun setPage(page: Long) {
        _state.update {
            it.copy(
                page = page
            )
        }
    }
}

data class LeaderboardEntry(
    val rank: Long,
    @SerializedName("total_seconds") val totalSeconds: Long,
    val user: ListCurrentlyHackingUsers.User? = null,
    val leaderboardUser: GetLeaderboardResponse.Entry.User,
    val isSelf: Boolean
)