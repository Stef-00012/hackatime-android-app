package com.stefdp.hackatime.screens.home

import android.content.Context
import android.util.Log
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.gson.annotations.SerializedName
import com.stefdp.hackatime.LocalLoggedUser
import com.stefdp.hackatime.components.Popup
import com.stefdp.hackatime.network.hackatimeapi.models.Feature
import com.stefdp.hackatime.network.hackatimeapi.requests.getCurrentUserStats
import com.stefdp.hackatime.network.hackatimeapi.requests.getCurrentUserStatsLast7Days
import com.stefdp.hackatime.screens.LoginScreen
import com.stefdp.hackatime.screens.home.components.Container
import com.stefdp.hackatime.ui.theme.HackatimeStatsTheme
import com.stefdp.hackatime.utils.DayData
import com.stefdp.hackatime.utils.GeneralStat
import com.stefdp.hackatime.utils.formatMs
import com.stefdp.hackatime.utils.getLast7DaysData
import com.stefdp.hackatime.utils.getTop
import com.stefdp.hackatime.utils.shimmerable
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorCount
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.Pie
import ir.ehsannarmani.compose_charts.models.PopupProperties
import ir.ehsannarmani.compose_charts.models.StrokeStyle
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.time.Clock

// TODO("Apparently home is inserted twice in the navigation stack")

@Composable
fun HomeScreen(
    navController: NavHostController,
    context: Context
) {
    val localUserStats = LocalLoggedUser.current
    val scrollState = rememberScrollState()

    if (localUserStats == null) {
        navController.navigate(LoginScreen) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
        }
    }

    var last7DaysData by remember { mutableStateOf<List<DayData>>(emptyList()) }

    var statsRange by rememberSaveable { mutableStateOf(Range.LAST_SEVEN_DAYS) }
    var rangeStart by rememberSaveable { mutableStateOf("") }
    var rangeEnd by rememberSaveable { mutableStateOf("") }

    var totalSeconds by remember { mutableDoubleStateOf(0.0) }
    var topProject by remember { mutableStateOf<GeneralStat?>(null) }
    var topLanguage by remember { mutableStateOf<GeneralStat?>(null) }
    var topOperatingSystem by remember { mutableStateOf<GeneralStat?>(null) }
    var topEditor by remember { mutableStateOf<GeneralStat?>(null) }
    var topMachine by remember { mutableStateOf<GeneralStat?>(null) }

    LaunchedEffect(statsRange) {
         when (statsRange) {
            Range.LAST_SEVEN_DAYS -> {
                val currentUserStats = getCurrentUserStatsLast7Days(
                    context = context,
                    features = listOf(
                        Feature.PROJECTS,
                        Feature.LANGUAGES,
                        Feature.OPERATING_SYSTEMS,
                        Feature.EDITORS,
                        Feature.MACHINES,
                    )
                )

                if (currentUserStats.isSuccess) {
                    val stats = currentUserStats.getOrNull() ?: return@LaunchedEffect

                    totalSeconds = stats.totalSeconds
                    topProject = getTop(stats.projects)
                    topLanguage = getTop(stats.languages)
                    topOperatingSystem = getTop(stats.operatingSystems)
                    topEditor = getTop(stats.editors)
                    topMachine = getTop(stats.machines)
                }
            }
            Range.ALL_TIME -> {
                val currentUserStats = getCurrentUserStats(
                    context = context,
                    features = listOf(
                        Feature.PROJECTS,
                        Feature.LANGUAGES,
                    )
                )

                if (currentUserStats.isSuccess) {
                    val stats = currentUserStats.getOrNull() ?: return@LaunchedEffect

                    totalSeconds = stats.totalSeconds
                    topProject = getTop(stats.projects)
                    topLanguage = getTop(stats.languages)
                }
            }
            Range.CUSTOM, Range.ONE_DAY -> {
                val currentUserStats = getCurrentUserStats(
                    context = context,
                    features = listOf(
                        Feature.PROJECTS,
                        Feature.LANGUAGES,
                    ),
                    startDate = rangeStart,
                    endDate = rangeEnd
                )

                if (currentUserStats.isSuccess) {
                    val stats = currentUserStats.getOrNull() ?: return@LaunchedEffect

                    totalSeconds = stats.totalSeconds
                    topProject = getTop(stats.projects)
                    topLanguage = getTop(stats.languages)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        val stats = getLast7DaysData(
            context = context
        )

        last7DaysData = stats
    }

    // maybe LazyColumn?
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        var showRangePopup by rememberSaveable { mutableStateOf(false) }
        val rangeText = when (statsRange) {
            Range.LAST_SEVEN_DAYS -> "Last 7 Days"
            Range.ALL_TIME -> "All Time"
            Range.CUSTOM -> "$rangeStart - $rangeEnd"
            Range.ONE_DAY -> rangeStart
        }

        OutlinedButton(
            enabled = true,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
            onClick = { showRangePopup = true },
            border = BorderStroke(
                color = MaterialTheme.colorScheme.primary,
                width = 2.dp
            )
        ) {
            Text(
                text = "Range: $rangeText",
                color = MaterialTheme.colorScheme.primary
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
                    val oneDayMillis = 24 * 60 * 60 * 1000L

                    isOneDay = true

                    dateRangePickerState.selectedEndDateMillis?.plus(oneDayMillis)
                } else dateRangePickerState.selectedEndDateMillis

                Log.d("HomeScreen", "Selected Start Date: $startDate, Selected End Date: $endDate")

                if (startDate == null || endDate == null) return@LaunchedEffect

                val startDateString = startDate.let { millis ->
                    val instant = Instant.ofEpochMilli(millis)
                    val formatter = DateTimeFormatter.ISO_LOCAL_DATE

                    instant.atZone(ZoneOffset.UTC).format(formatter)
                }

                val endDateString = endDate.let { millis ->
                    val instant = Instant.ofEpochMilli(millis)
                    val formatter = DateTimeFormatter.ISO_LOCAL_DATE

                    instant.atZone(ZoneOffset.UTC).format(formatter)
                }

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
                    Text("Select Date Range")
                },
                title = {},
            )

            Button(
                modifier = Modifier.fillMaxWidth(1f).padding(start = 5.dp, end = 5.dp, top = 8.dp, bottom = 0.dp),
                onClick = {
                    statsRange = Range.LAST_SEVEN_DAYS
                    showRangePopup = false
                }
            ) {
                Text("Show Last 7 Days")
            }

            Button(
                modifier = Modifier.fillMaxWidth(1f).padding(start = 5.dp, end = 5.dp, top = 8.dp, bottom = 0.dp),
                onClick = {
                    statsRange = Range.ALL_TIME
                    showRangePopup = false
                }
            ) {
                Text("Show All Time")
            }

            Button(
                modifier = Modifier.fillMaxWidth(1f).padding(start = 5.dp, end = 5.dp, top = 8.dp, bottom = 0.dp),
                onClick = {
                    showRangePopup = false
                }
            ) {
                Text("Close")
            }
        }

        Container(
            modifier = Modifier.padding(
                start = 5.dp,
                end = 5.dp,
                top = 5.dp,
                bottom = 2.5.dp
            )
        ) {
            Text(
                text = "Total Time",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = formatMs(totalSeconds * 1000L),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        }

        Container(
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.5.dp)
        ) {
            Text(
                text = "Top Project",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = topProject?.name ?: "Unknown Project",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        }

        Container(
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.5.dp)
        ) {
            Text(
                text = "Top Language",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = topLanguage?.name ?: "Unknown Language",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (statsRange == Range.LAST_SEVEN_DAYS) {
            Container(
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.5.dp)
            ) {
                Text(
                    text = "Top OS",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = topOperatingSystem?.name ?: "Unknown OS",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Container(
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.5.dp)
            ) {
                Text(
                    text = "Top Editor",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = topEditor?.name ?: "Unknown Editor",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Container(
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.5.dp)
            ) {
                Text(
                    text = "Top Machine",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = topMachine?.name ?: "Unknown Machine",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Container(
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.5.dp)
            ) {
                Text(
                    text = "Last 7 Days Overview",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                val primaryColor = MaterialTheme.colorScheme.primary
                val chartHeight = 250.dp

                if (last7DaysData.isNotEmpty()) {
                    val lines = listOf(
                        Line(
                            values = last7DaysData.map { it.data?.totalSeconds ?: 0.0 },
                            color = SolidColor(primaryColor),
                        )
                    )

                    val labels = last7DaysData.map { userData ->
                        userData.date.let { date ->
                            val weekDayNumber = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)
                                .dayOfWeek.value

                            when (weekDayNumber) {
                                1 -> "Mon"
                                2 -> "Tue"
                                3 -> "Wed"
                                4 -> "Thu"
                                5 -> "Fri"
                                6 -> "Sat"
                                7 -> "Sun"
                                else -> "N/A"
                            }
                        }
                    }

                    val axisProperties = GridProperties.AxisProperties(
                        enabled = true,
                        color = SolidColor(
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                        ),
                        lineCount = 7,
                        style = StrokeStyle.Dashed(
                            intervals = floatArrayOf(15f, 15f),
                        )
                    )

                    LineChart(
                        data = lines,
                        modifier = Modifier.height(chartHeight),
                        dotsProperties = DotProperties(
                            enabled = true,
                            color = SolidColor(primaryColor),
                            strokeWidth = 1.dp,
                            radius = 4.dp,
                            strokeColor = SolidColor(primaryColor),
                        ),
                        labelProperties = LabelProperties(
                            enabled = true,
                            labels = labels,
                            rotation = LabelProperties.Rotation(
                                degree = 0f
                            ),
                            padding = 4.dp,
                            textStyle = MaterialTheme.typography.labelLarge.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ),
                        popupProperties = PopupProperties(
                            enabled = true,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            textStyle = MaterialTheme.typography.labelLarge.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            contentBuilder = { value ->
                                formatMs(
                                    ms = value.value * 1000L,
                                    limit = 2
                                )
                            }
                        ),
                        gridProperties = GridProperties(
                            enabled = true,
                            xAxisProperties = axisProperties,
                            yAxisProperties = axisProperties
                        ),
                        indicatorProperties = HorizontalIndicatorProperties(
                            enabled = true,
                            textStyle = MaterialTheme.typography.labelLarge.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                            count = IndicatorCount.CountBased(7),
                            contentBuilder = { value ->
                                formatMs(
                                    ms = value * 1000L,
                                    limit = 2
                                )
                            }
                        )
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(chartHeight).shimmerable(true)
                    )
                }
            }
        }

        // languages pie chart

        if (statsRange == Range.LAST_SEVEN_DAYS) {
            // editors pie graph

            // OS pie graph

            // machines pie graph
        }
    }
}

enum class Range(val value: String) {
    @SerializedName("7d")
    LAST_SEVEN_DAYS("7d"),

    @SerializedName("alltime")
    ALL_TIME("alltime"),

    @SerializedName("custom")
    CUSTOM("custom"),

    @SerializedName("1d")
    ONE_DAY("1d");

    override fun toString(): String = value
}

//@Preview
@Composable
fun TestPieChart() {
    var data by remember {
        mutableStateOf(
            listOf(
                Pie(
                    label = "Android",
                    data = 90.0,
                    color = Color.Red,
                    selectedColor = Color.Green
                ),
                Pie(
                    label = "Windows",
                    data = 90.0,
                    color = Color.Cyan,
                    selectedColor = Color.Blue
                ),
                Pie(
                    label = "Linux",
                    data = 90.0,
                    color = Color.Gray,
                    selectedColor = Color.Yellow
                ),
            )
        )
    }
    PieChart(
        modifier = Modifier.size(200.dp),
        data = data,
        onPieClick = {
            println("${it.label} Clicked")
            val pieIndex = data.indexOf(it)
            data.mapIndexed { mapIndex, pie -> pie.copy(selected = pieIndex == mapIndex) }
        },
//        selectedScale = 1.2f,
//        scaleAnimEnterSpec = spring(
//            dampingRatio = Spring.DampingRatioMediumBouncy,
//            stiffness = Spring.StiffnessLow
//        ),
        colorAnimEnterSpec = tween(300),
        colorAnimExitSpec = tween(300),
        scaleAnimExitSpec = tween(300),
        spaceDegreeAnimExitSpec = tween(300),
        style = Pie.Style.Fill,
    )

    LineChart(
        modifier = Modifier.fillMaxSize().padding(horizontal = 22.dp),
        data = remember {
            listOf(
                Line(
                    label = "Windows",
                    values = listOf(28.0, 41.0, 5.0, 10.0, 35.0),
                    color = SolidColor(Color(0xFF23af92)),
                    firstGradientFillColor = Color(0xFF2BC0A1).copy(alpha = .5f),
                    secondGradientFillColor = Color.Transparent,
                    strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                    gradientAnimationDelay = 1000,
                    drawStyle = DrawStyle.Stroke(width = 2.dp),
                )
            )
        },
//        animationMode = AnimationMode.Together(delayBuilder = {
//            it * 500L
//        }),
        animationMode = AnimationMode.OneByOne
    )
}

//@Preview(showSystemUi = false, showBackground = true)
@Composable
fun TestBarChart() {
    ColumnChart(
        modifier = Modifier.fillMaxSize().padding(horizontal = 22.dp).height(400.dp),
        data = remember {
            listOf(
                Bars(
                    label = "Jan",
                    values = listOf(
                        Bars.Data(
                            label = "Linux",
                            value = 50.0,
                            color = Brush.verticalGradient(
                                0.1f to Color(0xFF2BC0A1).copy(alpha = .5f),
                                0.9f to Color.Transparent,
                                100f to Color(0xFF23af92).copy(alpha = .5f)
                            )
                        ),
                        Bars.Data(label = "Windows", value = 70.0, color = SolidColor(Color.Red))
                    ),
                ),
                Bars(
                    label = "Feb",
                    values = listOf(
                        Bars.Data(label = "Linux", value = 80.0, color = Brush.verticalGradient(
                            0.1f to Color(0xFF23af92).copy(alpha = .5f),
                            0.9f to Color.Transparent,
                            100f to Color(0xFF2BC0A1).copy(alpha = .5f)
                        )),
                        Bars.Data(label = "Windows", value = 60.0, color = SolidColor(Color.Red))
                    ),
                )
            )
        },
        barProperties = BarProperties(
//            radius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
            spacing = 3.dp,
            thickness = 20.dp
        ),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
    )
}

//@Preview
@Composable
fun TestLineChart() {
    val primaryColor = MaterialTheme.colorScheme.primary

    val lines by remember { mutableStateOf(
        listOf(
            Line(
                values = listOf(
                    20520.0,
                    28920.0,
                    12600.0,
                    11100.0,
                    8100.0,
                    10200.0,
                    3900.0
                ),
                color = SolidColor(primaryColor),
            )
        )
    ) }

    val labels = listOf(
        "Sun",
        "Mon",
        "Tue",
        "Wed",
        "Thu",
        "Fri",
        "Sat",
    )

    val axisProperties = GridProperties.AxisProperties(
        enabled = true,
        color = SolidColor(
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
        ),
        lineCount = 7,
        style = StrokeStyle.Dashed(
            intervals = floatArrayOf(15f, 15f),
        )
    )

    LineChart(
        data = lines,
        modifier = Modifier.height(250.dp),
        dotsProperties = DotProperties(
            enabled = true,
            color = SolidColor(primaryColor),
            strokeWidth = 1.dp,
            radius = 4.dp,
            strokeColor = SolidColor(primaryColor),
        ),
        labelProperties = LabelProperties(
            enabled = true,
            labels = labels,
            rotation = LabelProperties.Rotation(
                degree = 0f
            ),
            padding = 4.dp,
            textStyle = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ),
        popupProperties = PopupProperties(
            enabled = true,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            textStyle = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            contentBuilder = { value ->
                formatMs(
                    ms = value.value * 1000L,
                    limit = 2
                )
            }
        ),
        gridProperties = GridProperties(
            enabled = true,
            xAxisProperties = axisProperties,
            yAxisProperties = axisProperties
        ),
        indicatorProperties = HorizontalIndicatorProperties(
            enabled = true,
            textStyle = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            count = IndicatorCount.CountBased(7),
            contentBuilder = { value ->
                formatMs(
                    ms = value * 1000L,
                    limit = 2
                )
            }
        )
    )
}

//@Preview
@Composable
fun TestDatePicker() {
    HackatimeStatsTheme {
        val today = Clock.System.now().toEpochMilliseconds()

        val selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= today
            }
        }

        val dateRangePickerState = rememberDateRangePickerState(
            selectableDates = selectableDates
        )

        DateRangePicker(
            state = dateRangePickerState,
            modifier = Modifier.height(400.dp),
            colors = DatePickerDefaults.colors().copy(
                containerColor = Color.Transparent,
                dayInSelectionRangeContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
            ),
            headline = {
                Text("Select Date Range")
            },
            title = {},
        )
    }
}