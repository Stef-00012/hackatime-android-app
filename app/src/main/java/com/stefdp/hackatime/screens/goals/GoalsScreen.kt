package com.stefdp.hackatime.screens.goals

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import com.stefdp.hackatime.components.Button
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import com.stefdp.hackatime.components.OutlinedButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.gson.annotations.SerializedName
import com.stefdp.hackatime.R
import com.stefdp.hackatime.components.Notification
import com.stefdp.hackatime.components.Popup
import com.stefdp.hackatime.components.PullToRefreshBox
import com.stefdp.hackatime.components.TextInput
import com.stefdp.hackatime.screens.HomeScreen
import com.stefdp.hackatime.screens.goals.components.GoalContainer
import com.stefdp.hackatime.utils.shimmerable
import com.stefdp.hackatime.utils.verticalLazyScrollbar
import com.stefdp.hackatime.utils.verticalScrollWithScrollbar
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days

@Composable
fun GoalsScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity,
    viewModel: GoalsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.updateIsApiOnServer(context)
    }

    if (!state.isApiOnServer) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.key_off),
                contentDescription = stringResource(R.string.data_not_on_server_title),
                modifier = Modifier.size(50.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = stringResource(R.string.data_not_on_server_title),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = stringResource(R.string.data_not_on_server_message),
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.weight(0.7f)
            )

            TextButton(
                onClick = {
                    navController.navigate(HomeScreen)
                }
            ) {
                Text(
                    text = stringResource(R.string.api_key_not_on_server_home_button),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )
        }

        return
    }

    fun reload(isRefresh: Boolean = false) {
        viewModel.updateGoals(
            context = context,
            isRefresh = isRefresh
        )
    }

    LaunchedEffect(Unit) {
        reload()
    }

    LaunchedEffect(state.statsRange, state.rangeStart, state.rangeEnd) {
        reload()
    }

    Column {
        OutlinedButton(
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
            onClick = {
                viewModel.showRangePopup()
            },
        ) {
            Text(
                text = stringResource(R.string.date_range, state.rangeText),
                color = LocalContentColor.current,
                fontWeight = FontWeight.Bold
            )
        }

        Popup(
            showPopup = state.showRangePopup,
            onDismissRequest = {
                viewModel.hideRangePopup()
            },
        ) {
            val today = Clock.System.now().toEpochMilliseconds()

            val selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis <= today
                }
            }

            val dateRangePickerState = rememberDateRangePickerState(
                selectableDates = selectableDates,
            )

            LaunchedEffect(
                dateRangePickerState.selectedStartDateMillis,
                dateRangePickerState.selectedEndDateMillis
            ) {
                var isOneDay = false

                val startDate = dateRangePickerState.selectedStartDateMillis
                val endDate = if (dateRangePickerState.selectedEndDateMillis == startDate) {
                    val oneDayMillis = 1.days.inWholeMilliseconds // 24 * 60 * 60 * 1000L

                    isOneDay = true

                    dateRangePickerState.selectedEndDateMillis?.plus(oneDayMillis)
                } else dateRangePickerState.selectedEndDateMillis

                if (startDate == null || endDate == null) return@LaunchedEffect

                val startDateString = Instant
                    .ofEpochMilli(startDate)
                    .atZone(ZoneOffset.UTC)
                    .format(DateTimeFormatter.ISO_LOCAL_DATE)

                val endDateString = Instant
                    .ofEpochMilli(endDate)
                    .atZone(ZoneOffset.UTC)
                    .format(DateTimeFormatter.ISO_LOCAL_DATE)

                if (startDateString.isNullOrEmpty() || endDateString.isNullOrEmpty()) return@LaunchedEffect

                viewModel.setRangeStart(startDateString)
                viewModel.setRangeEnd(endDateString)
                viewModel.setStatsRange(if (isOneDay) Range.ONE_DAY else Range.CUSTOM)
                viewModel.hideRangePopup()
            }

            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier.height(350.dp),
                colors = DatePickerDefaults.colors().copy(
                    containerColor = Color.Transparent,
                    dayInSelectionRangeContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                ),
                headline = {
                    Text(
                        text = stringResource(R.string.select_date_range)
                    )
                },
                title = {},
            )

            Button(
                modifier = Modifier.fillMaxWidth(1f).padding(start = 5.dp, end = 5.dp, top = 8.dp, bottom = 0.dp),
                onClick = {
                    viewModel.setStatsRange(Range.ALL_TIME)
                    viewModel.hideRangePopup()
                },
                enabled = state.statsRange != Range.ALL_TIME
            ) {
                Text(
                    text = stringResource(R.string.date_range_all_time),
                    color = LocalContentColor.current,
                    fontWeight = FontWeight.Bold,
                )
            }

            Button(
                modifier = Modifier.fillMaxWidth(1f).padding(start = 5.dp, end = 5.dp, top = 8.dp, bottom = 0.dp),
                onClick = {
                    viewModel.hideRangePopup()
                }
            ) {
                Text(
                    text = stringResource(R.string.close_button),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        OutlinedButton(
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
            onClick = {
                viewModel.showUpdateGoalPopup()
            },
        ) {
            Text(
                text = stringResource(R.string.update_goal_button),
                color = LocalContentColor.current,
                fontWeight = FontWeight.Bold
            )
        }

        Popup(
            showPopup = state.showUpdateGoalPopup,
            onDismissRequest = {
                viewModel.hideUpdateGoalPopup()
            },
        ) {
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.update_goal_popup_title),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                TextInput(
                    value = state.newGoal,
                    onValueChange = {
                        viewModel.setNewGoal(it)
                    },
                    placeholder = "5h",
                    label = stringResource(R.string.update_goal_input_label),
                )

                Button(
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    onClick = {
                        viewModel.saveNewGoal(
                            context = context,
                            onError = { error ->
                                Notification.show(
                                    activity = activity,
                                    duration = 3000L
                                ) {
                                    Text(
                                        text = error,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            onSuccess = {
                                Notification.show(
                                    activity = activity,
                                    duration = 3000L
                                ) {
                                    Text(
                                        text = stringResource(R.string.goal_update_success_message)
                                    )
                                }
                            }
                        )
                    },
                    enabled = !state.isLoading
                ) {
                    AnimatedContent(
                        targetState = state.isLoading,
                        transitionSpec = {
                            (
                                fadeIn() + slideInVertically { height -> height }
                            ) togetherWith (
                                fadeOut() + slideOutVertically { height -> -height }
                            )
                        },
                        label = "UpdateButtonAnimation"
                    ) { isLoading ->
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = LocalContentColor.current
                            )

                            Spacer(
                                modifier = Modifier.width(16.dp)
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.save),
                                contentDescription = stringResource(R.string.save_notifications_preferences_content_description)
                            )

                            Spacer(
                                modifier = Modifier.width(5.dp)
                            )
                        }
                    }

                    Text(
                        text = stringResource(R.string.save_button),
                        fontWeight = FontWeight.Bold,
                        color = LocalContentColor.current
                    )
                }
            }
        }

        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = {
                reload(true)
            }
        ) {
            val scrollState = rememberScrollState()

            if (state.isLoading || state.goals == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScrollWithScrollbar(scrollState)
                ) {
                    val repeatCount = 6

                    repeat(repeatCount) {
                        SkeletonGoal(
                            index = it,
                            size = repeatCount
                        )
                    }
                }
            } else if (state.goals!!.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScrollWithScrollbar(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.no_goals_message),
                        modifier = Modifier.fillMaxWidth().padding(top = 60.dp),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            } else {
                val listState = rememberLazyListState()

                LazyColumn(
                    state = listState,
                    modifier = Modifier.verticalLazyScrollbar(listState)
                ) {
                    items(count = state.goals!!.size) { index ->
                        GoalContainer(
                            modifier = Modifier.padding(
                                start = 10.dp,
                                end = 10.dp,
                                top = if (index == 0) 10.dp else 5.dp,
                                bottom = if (index == state.goals!!.size - 1) 10.dp else 5.dp
                            ),
                            goal = state.goals!![index],
                            context = context
                        )
                    }
                }
            }
        }
    }
}

enum class Range(val value: String) {
    @SerializedName("alltime")
    ALL_TIME("alltime"),

    @SerializedName("custom")
    CUSTOM("custom"),

    @SerializedName("1d")
    ONE_DAY("1d");

    override fun toString(): String = value
}

@Composable
fun SkeletonGoal(index: Int, size: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 10.dp,
                end = 10.dp,
                top = if (index == 0) 10.dp else 5.dp,
                bottom = if (index == size - 1) 10.dp else 5.dp
            )
            .height(120.dp)
            .shimmerable(
                enabled = true,
                shape = RoundedCornerShape(10.dp)
            )
    ) {}
}