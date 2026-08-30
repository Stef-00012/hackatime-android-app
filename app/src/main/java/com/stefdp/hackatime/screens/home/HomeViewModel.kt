package com.stefdp.hackatime.screens.home

import android.content.Context
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.hackatime.R
import com.stefdp.hackatime.network.hackatimeapi.models.Feature
import com.stefdp.hackatime.network.hackatimeapi.requests.getUserStats
import com.stefdp.hackatime.network.hackatimeapi.requests.getWakatimeUserLast7DaysStats
import com.stefdp.hackatime.utils.DayData
import com.stefdp.hackatime.utils.GeneralStat
import com.stefdp.hackatime.utils.getLast7DaysData
import com.stefdp.hackatime.utils.getTop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val last7DaysData: List<DayData> = emptyList(),
    val statsRange: Range = Range.LAST_SEVEN_DAYS,
    val rangeStart: String = "",
    val rangeEnd: String = "",
    val totalSeconds: Long = 0L,
    val topProject: GeneralStat? = null,
    val topLanguage: GeneralStat? = null,
    val topOperatingSystem: GeneralStat? = null,
    val topEditor: GeneralStat? = null,
    val topMachine: GeneralStat? = null,
    val languages: List<GeneralStat> = emptyList(),
    val editors: List<GeneralStat> = emptyList(),
    val operatingSystems: List<GeneralStat> = emptyList(),
    val machines: List<GeneralStat> = emptyList(),
    val showRangePopup: Boolean = false,
    val rangeText: String = ""
)

private const val TAG = "HomeViewModel"

class HomeViewModel : ViewModel() {
    private val _state: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    fun init(
        context: Context,
        isRefresh: Boolean
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    isRefreshing = isRefresh
                )
            }

            when (_state.value.statsRange) {
                Range.LAST_SEVEN_DAYS -> {
                    val currentUserStats = getWakatimeUserLast7DaysStats(
                        context = context,
                        userId = "current"
                    )

                    if (currentUserStats.isFailure) {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false
                            )
                        }

                        return@launch
                    }

                    val stats = currentUserStats.getOrNull()

                    if (stats == null) {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false
                            )
                        }

                        return@launch
                    }

                    updateLast7DaysData(context)

                    _state.update {
                        it.copy(
                            totalSeconds = stats.totalSeconds,
                            topProject = getTop(stats.projects),
                            topLanguage = getTop(stats.languages),
                            topOperatingSystem = getTop(stats.operatingSystems),
                            topEditor = getTop(stats.editors),
                            topMachine = getTop(stats.machines),
                            languages = stats.languages,
                            editors = stats.editors,
                            operatingSystems = stats.operatingSystems,
                            machines = stats.machines,
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }

                Range.ALL_TIME -> {
                    val currentUserStats = getUserStats(
                        context = context,
                        username = "my",
                        features = listOf(
                            Feature.LANGUAGES,
                            Feature.PROJECTS
                        )
                    )

                    if (currentUserStats.isFailure) {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false
                            )
                        }

                        return@launch
                    }

                    val stats = currentUserStats.getOrNull()?.data

                    if (stats == null) {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false
                            )
                        }

                        return@launch
                    }

                    _state.update {
                        it.copy(
                            totalSeconds = stats.totalSeconds,
                            topProject = getTop(stats.projects),
                            topLanguage = getTop(stats.languages),
                            languages = stats.languages,
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }

                Range.CUSTOM, Range.ONE_DAY -> {
                    val currentUserStats = getUserStats(
                        context = context,
                        username = "my",
                        features = listOf(
                            Feature.LANGUAGES,
                            Feature.PROJECTS
                        ),
                        startDate = _state.value.rangeStart,
                        endDate = _state.value.rangeEnd
                    )

                    if (currentUserStats.isFailure) {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false
                            )
                        }

                        return@launch
                    }

                    val stats = currentUserStats.getOrNull()?.data

                    if (stats == null) {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false
                            )
                        }

                        return@launch
                    }

                    _state.update {
                        it.copy(
                            totalSeconds = stats.totalSeconds,
                            topProject = getTop(stats.projects),
                            topLanguage = getTop(stats.languages),
                            languages = stats.languages,
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
            }

            updateRangeText(context)
        }
    }

    private suspend fun updateLast7DaysData(
        context: Context
    ) {
        val stats = getLast7DaysData(
            context = context
        )

        _state.update {
            it.copy(
                last7DaysData = stats
            )
        }
    }

    fun setRangeStart(start: String) {
        _state.update {
            it.copy(
                rangeStart = start
            )
        }
    }

    fun setRangeEnd(end: String) {
        _state.update {
            it.copy(
                rangeEnd = end
            )
        }
    }

    fun setStatsRange(range: Range) {
        _state.update {
            it.copy(
                statsRange = range
            )
        }
    }

    private fun updateRangeText(
        context: Context
    ) {
        _state.update {
            it.copy(
                rangeText = when (_state.value.statsRange) {
                    Range.LAST_SEVEN_DAYS -> context.getString(R.string.date_range_last_7_days)
                    Range.ALL_TIME -> context.getString(R.string.date_range_all_time)
                    Range.CUSTOM -> "${_state.value.rangeStart} - ${_state.value.rangeEnd}"
                    Range.ONE_DAY -> _state.value.rangeStart
                }
            )
        }
    }

    fun showRangePopup() {
        _state.update {
            it.copy(
                showRangePopup = true
            )
        }
    }

    fun hideRangePopup() {
        _state.update {
            it.copy(
                showRangePopup = false
            )
        }
    }
}