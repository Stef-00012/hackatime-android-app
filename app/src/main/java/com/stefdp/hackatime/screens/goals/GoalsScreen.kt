package com.stefdp.hackatime.screens.goals

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.google.gson.annotations.SerializedName
import com.stefdp.hackatime.R
import com.stefdp.hackatime.components.Popup
import com.stefdp.hackatime.components.TextInput
import com.stefdp.hackatime.network.backendapi.models.Goal
import com.stefdp.hackatime.network.backendapi.requests.getUser
import com.stefdp.hackatime.network.backendapi.requests.getUserGoals
import com.stefdp.hackatime.network.backendapi.requests.updateUserGoal
import com.stefdp.hackatime.screens.HomeScreen
import com.stefdp.hackatime.screens.goals.components.GoalContainer
import com.stefdp.hackatime.utils.parseTimeToMillis
import com.stefdp.hackatime.utils.shimmerable
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@Composable
fun GoalsScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity
) {
    var isAPiOnServer by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isAPiOnServer = getUser(
            context = context
        )
    }

    if (!isAPiOnServer) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.key_off),
                contentDescription = stringResource(R.string.api_key_not_on_server_title),
                modifier = Modifier.size(50.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = stringResource(R.string.api_key_not_on_server_title),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = stringResource(R.string.api_key_not_on_server_message),
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
                    )
                )
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )
        }

        return
    }

    var statsRange by rememberSaveable { mutableStateOf(Range.CUSTOM) }
    var rangeStart by rememberSaveable { mutableStateOf(
        Instant
            .now()
            .minus(Duration.ofDays(7))
            .atZone(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_LOCAL_DATE)
    ) }
    var rangeEnd by rememberSaveable { mutableStateOf(
        Instant
            .now()
            .atZone(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_LOCAL_DATE)
    ) }

    var showRangePopup by rememberSaveable { mutableStateOf(false) }
    var showUpdateGoalPopup by rememberSaveable { mutableStateOf(false) }

    val rangeText = when (statsRange) {
        Range.ALL_TIME -> stringResource(R.string.date_range_all_time)
        Range.CUSTOM -> "$rangeStart - $rangeEnd"
        Range.ONE_DAY -> rangeStart
    }

    var goals by remember { mutableStateOf<List<Goal>?>(null) }

    LaunchedEffect(Unit) {
        goals = getUserGoals(
            context = context,
            startDate = rangeStart,
            endDate = rangeEnd
        )
    }

    suspend fun updateUserGoals() {
        when (statsRange) {
            Range.ALL_TIME -> {
                goals = getUserGoals(
                    context = context,
                    all = true
                )
            }
            Range.CUSTOM, Range.ONE_DAY -> {
                goals = getUserGoals(
                    context = context,
                    startDate = rangeStart,
                    endDate = rangeEnd
                )
            }
        }
    }

    LaunchedEffect(statsRange, rangeStart, rangeEnd) {
        updateUserGoals()
    }

    Column {
        OutlinedButton(
            enabled = true,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
            onClick = { showRangePopup = true },
            colors = ButtonDefaults.outlinedButtonColors().copy(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            border = BorderStroke(
                color = MaterialTheme.colorScheme.primary,
                width = 2.dp
            ),
        ) {
            Text(
                text = stringResource(R.string.date_range, rangeText)
            )
        }

        Popup(
            showPopup = showRangePopup,
            onDismissRequest = { showRangePopup = false },
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

                rangeStart = startDateString
                rangeEnd = endDateString
                statsRange = if (isOneDay) Range.ONE_DAY else Range.CUSTOM
                showRangePopup = false
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
                    statsRange = Range.ALL_TIME
                    showRangePopup = false
                }
            ) {
                Text(
                    text = stringResource(R.string.date_range_all_time)
                )
            }

            Button(
                modifier = Modifier.fillMaxWidth(1f).padding(start = 5.dp, end = 5.dp, top = 8.dp, bottom = 0.dp),
                onClick = {
                    showRangePopup = false
                }
            ) {
                Text(
                    text = stringResource(R.string.close_button)
                )
            }
        }

        OutlinedButton(
            enabled = true,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
            onClick = { showUpdateGoalPopup = true },
            colors = ButtonDefaults.outlinedButtonColors().copy(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            border = BorderStroke(
                color = MaterialTheme.colorScheme.primary,
                width = 2.dp
            )
        ) {
            Text(
                text = stringResource(R.string.update_goal_button),
            )
        }

        Popup(
            showPopup = showUpdateGoalPopup,
            onDismissRequest = { showUpdateGoalPopup = false },
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

                var newGoal by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }

                TextInput(
                    value = newGoal,
                    onValueChange = { newGoal = it },
                    placeholder = "5h",
                    label = stringResource(R.string.update_goal_input_label),
                )

                val coroutineScope = rememberCoroutineScope()

                Button(
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    onClick = {
                        coroutineScope.launch {
                            val newGoalDuration = parseTimeToMillis(newGoal.text)

                            if (newGoalDuration == null || newGoalDuration < 1.minutes.inWholeMilliseconds || newGoalDuration > 23.hours.inWholeMilliseconds) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.invalid_goal_error),
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                val success = updateUserGoal(
                                    context = context,
                                    date = Instant.now().atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE),
                                    goal = newGoalDuration / 1000
                                )

                                if (!success) {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.goal_update_fail_message),
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.goal_update_success_message),
                                        Toast.LENGTH_LONG
                                    ).show()

                                    updateUserGoals()

                                    showUpdateGoalPopup = false
                                }
                            }
                        }
                    }
                ) {
                    Text(
                        text = stringResource(R.string.save_button),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        if (goals == null) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                val repeatCount = 5

                repeat(repeatCount) {
                    SkeletonGoal(
                        index = it,
                        size = repeatCount
                    )
                }
            }
        } else if (goals!!.isEmpty()) {
            Text(
                text = stringResource(R.string.no_goals_message),
                modifier = Modifier.fillMaxWidth().padding(top = 60.dp),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )
        } else {
            LazyColumn() {
                items(count = goals!!.size) { index ->
                    GoalContainer(
                        modifier = Modifier.padding(
                            start = 10.dp,
                            end = 10.dp,
                            top = if (index == 0) 10.dp else 5.dp,
                            bottom = if (index == goals!!.size - 1) 10.dp else 5.dp
                        ),
                        goal = goals!![index]
                    )
                }
            }
        }
    }
}

private enum class Range(val value: String) {
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
            .fillMaxSize()
            .padding(
                start = 10.dp,
                end = 10.dp,
                top = if (index == 0) 10.dp else 5.dp,
                bottom = if (index == size - 1) 10.dp else 5.dp
            )
            .height(200.dp)
            .shimmerable(
                enabled = true,
                shape = RoundedCornerShape(10.dp)
            )
    ) {}
}