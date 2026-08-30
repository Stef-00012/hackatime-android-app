package com.stefdp.hackatime.screens.goals

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefdp.hackatime.R
import com.stefdp.hackatime.network.backendapi.models.Goal
import com.stefdp.hackatime.network.backendapi.requests.getUser
import com.stefdp.hackatime.network.backendapi.requests.getUserGoals
import com.stefdp.hackatime.network.backendapi.requests.updateUserGoal
import com.stefdp.hackatime.utils.parseTimeToMillis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

data class GoalsUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isApiOnServer: Boolean = false,
    val statsRange: Range = Range.CUSTOM,
    val rangeStart: String = Instant
        .now()
        .minus(Duration.ofDays(7))
        .atZone(ZoneOffset.UTC)
        .format(DateTimeFormatter.ISO_LOCAL_DATE),
    val rangeEnd: String = Instant
        .now()
        .atZone(ZoneOffset.UTC)
        .format(DateTimeFormatter.ISO_LOCAL_DATE),
    val showRangePopup: Boolean = false,
    val showUpdateGoalPopup: Boolean = false,
    val rangeText: String = "",
    val goals: List<Goal>? = null,
    val newGoal: TextFieldValue = TextFieldValue("")
)

private const val TAG = "GoalsViewModel"

class GoalsViewModel : ViewModel() {
    private val _state: MutableStateFlow<GoalsUiState> = MutableStateFlow(GoalsUiState())
    val state: StateFlow<GoalsUiState> = _state.asStateFlow()

    fun updateIsApiOnServer(context: Context) {
        viewModelScope.launch {
            val isApiOnServer = getUser(
                context = context
            )

            _state.update {
                it.copy(
                    isApiOnServer = isApiOnServer
                )
            }
        }
    }

    private fun updateRangeText(context: Context) {
        _state.update {
            it.copy(
                rangeText = when (_state.value.statsRange) {
                    Range.ALL_TIME -> context.getString(R.string.date_range_all_time)
                    Range.CUSTOM -> "${_state.value.rangeStart} - ${_state.value.rangeEnd}"
                    Range.ONE_DAY -> _state.value.rangeStart
                }
            )
        }
    }

    fun updateGoals(
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

            val goals = when (_state.value.statsRange) {
                Range.ALL_TIME -> {
                    getUserGoals(
                        context = context,
                        all = true
                    )
                }

                Range.CUSTOM, Range.ONE_DAY -> {
                    getUserGoals(
                        context = context,
                        startDate = _state.value.rangeStart,
                        endDate =_state.value. rangeEnd
                    )
                }
            }

            updateRangeText(context)

            _state.update {
                it.copy(
                    goals = goals,
                    isLoading = false,
                    isRefreshing = false
                )
            }
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

    fun setStatsRange(range: Range) {
        _state.update {
            it.copy(
                statsRange = range
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

    fun showUpdateGoalPopup() {
        _state.update {
            it.copy(
                showUpdateGoalPopup = true
            )
        }
    }

    fun hideUpdateGoalPopup() {
        _state.update {
            it.copy(
                showUpdateGoalPopup = false,
                newGoal = TextFieldValue(""),
            )
        }
    }

    fun setNewGoal(newGoal: TextFieldValue) {
        _state.update {
            it.copy(
                newGoal = newGoal
            )
        }
    }

    fun saveNewGoal(
        context: Context,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }

            val newGoalDuration = parseTimeToMillis(_state.value.newGoal.text.trim())

            if (newGoalDuration == null || newGoalDuration < 1.minutes.inWholeMilliseconds || newGoalDuration > 23.hours.inWholeMilliseconds) {
                onError(context.getString(R.string.invalid_goal_error))
            } else {
                val success = updateUserGoal(
                    context = context,
                    date = Instant.now().atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE),
                    goal = newGoalDuration / 1000
                )

                if (!success) {
                    onError(context.getString(R.string.goal_update_fail_message))
                } else {
                    onSuccess()

                    updateGoals(context, false)

                    hideRangePopup()
                }
            }

            _state.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }
}