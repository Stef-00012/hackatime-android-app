package com.stefdp.hackatime.screens.home

import android.content.Context
import android.util.Log
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.Alignment
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
import com.stefdp.hackatime.network.hackatimeapi.models.EditorLast7Days
import com.stefdp.hackatime.network.hackatimeapi.models.Feature
import com.stefdp.hackatime.network.hackatimeapi.models.Language
import com.stefdp.hackatime.network.hackatimeapi.models.MachineLast7Days
import com.stefdp.hackatime.network.hackatimeapi.models.OperatingSystemLast7Days
import com.stefdp.hackatime.network.hackatimeapi.requests.getCurrentUserStats
import com.stefdp.hackatime.network.hackatimeapi.requests.getCurrentUserStatsLast7Days
import com.stefdp.hackatime.screens.LoginScreen
import com.stefdp.hackatime.screens.home.components.Container
import com.stefdp.hackatime.ui.theme.HackatimeStatsTheme
import com.stefdp.hackatime.utils.DayData
import com.stefdp.hackatime.utils.GeneralStat
import com.stefdp.hackatime.utils.colorHash
import com.stefdp.hackatime.utils.formatMs
import com.stefdp.hackatime.utils.getLast7DaysData
import com.stefdp.hackatime.utils.getTop
import com.stefdp.hackatime.utils.languageColors
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

    var last7DaysData by rememberSaveable { mutableStateOf<List<DayData>>(emptyList()) }

    var statsRange by rememberSaveable { mutableStateOf(Range.LAST_SEVEN_DAYS) }
    var rangeStart by rememberSaveable { mutableStateOf("") }
    var rangeEnd by rememberSaveable { mutableStateOf("") }

    var totalSeconds by remember { mutableDoubleStateOf(0.0) }
    var topProject by remember { mutableStateOf<GeneralStat?>(null) }
    var topLanguage by remember { mutableStateOf<GeneralStat?>(null) }
    var topOperatingSystem by remember { mutableStateOf<GeneralStat?>(null) }
    var topEditor by remember { mutableStateOf<GeneralStat?>(null) }
    var topMachine by remember { mutableStateOf<GeneralStat?>(null) }

    var languages by remember { mutableStateOf<List<GeneralStat>>(emptyList()) }
    var editors by remember { mutableStateOf<List<EditorLast7Days>>(emptyList()) }
    var operatingSystems by remember { mutableStateOf<List<OperatingSystemLast7Days>>(emptyList()) }
    var machines by remember { mutableStateOf<List<MachineLast7Days>>(emptyList()) }

    LaunchedEffect(statsRange, rangeStart, rangeEnd) {
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
                    languages = stats.languages ?: emptyList()
                    editors = stats.editors ?: emptyList()
                    operatingSystems = stats.operatingSystems ?: emptyList()
                    machines = stats.machines ?: emptyList()
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
                    languages = stats.languages ?: emptyList()
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
                    languages = stats.languages ?: emptyList()
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
                    val lines by rememberSaveable(last7DaysData) { mutableStateOf(
                        listOf(
                            Line(
                                values = last7DaysData.map { it.data?.totalSeconds ?: 0.0 },
                                color = SolidColor(primaryColor),
                            )
                        )
                    ) }

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

        Container(
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.5.dp)
        ) {
            Text(
                text = "Languages",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 10.dp),
            )

            var selectedLanguage by remember { mutableStateOf<Pie?>(null) }
            var pieChartLanguages by remember(languages, selectedLanguage) { mutableStateOf(
                languages.map { language ->
                    Pie(
                        label = language.name,
                        data = language.totalSeconds,
                        color = languageColors[language.name.lowercase()] ?: colorHash(language.name),
                        selected = language.name == selectedLanguage?.label
                    )
                }
            ) }

            val chartSize = 250.dp

            if (pieChartLanguages.isNotEmpty()) {
                PieChart(
                    data = pieChartLanguages,
                    modifier = Modifier
                        .size(chartSize)
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 20.dp),
                    style = Pie.Style.Fill,
                    selectedScale = 1.2f,
                    onPieClick = { clickedPie ->
                        selectedLanguage = clickedPie
                    },
                )

                if (selectedLanguage != null && pieChartLanguages.find { it.label == selectedLanguage?.label } != null) {
                    Row {
                        Box(
                            modifier = Modifier
                                .size(15.dp)
                                .background(selectedLanguage?.color ?: Color(0xFFFFFFFF))
                                .align(Alignment.CenterVertically)
                        )

                        Spacer(
                            modifier = Modifier.width(6.dp)
                        )

                        Text(
                            text = "${selectedLanguage?.label}: ${formatMs(
                                ms = (selectedLanguage?.data ?: 0.0) * 1000L,
                                limit = 2
                            )}"
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(chartSize)
                        .shimmerable(
                            enabled = true,
                            shape = CircleShape,
                        )
                        .align(Alignment.CenterHorizontally)
                )
            }
        }

        if (statsRange == Range.LAST_SEVEN_DAYS) {
            Container(
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.5.dp)
            ) {
                Text(
                    text = "Editors",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 10.dp),
                )

                var selectedEditor by remember { mutableStateOf<Pie?>(null) }
                var pieChartEditors by remember(editors, selectedEditor) { mutableStateOf(
                    editors.map { editor ->
                        Pie(
                            label = editor.name,
                            data = editor.totalSeconds,
                            color = colorHash(editor.name),
                            selected = editor.name == selectedEditor?.label
                        )
                    }
                ) }

                val chartSize = 250.dp

                if (pieChartEditors.isNotEmpty()) {
                    PieChart(
                        data = pieChartEditors,
                        modifier = Modifier
                            .size(chartSize)
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 20.dp),
                        style = Pie.Style.Fill,
                        selectedScale = 1.2f,
                        onPieClick = { clickedPie ->
                            selectedEditor = clickedPie
                        },
                    )

                    if (selectedEditor != null && pieChartEditors.find { it.label == selectedEditor?.label } != null) {
                        Row {
                            Box(
                                modifier = Modifier
                                    .size(15.dp)
                                    .background(selectedEditor?.color ?: Color(0xFFFFFFFF))
                                    .align(Alignment.CenterVertically)
                            )

                            Spacer(
                                modifier = Modifier.width(6.dp)
                            )

                            Text(
                                text = "${selectedEditor?.label}: ${formatMs(
                                    ms = (selectedEditor?.data ?: 0.0) * 1000L,
                                    limit = 2
                                )}"
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(chartSize)
                            .shimmerable(
                                enabled = true,
                                shape = CircleShape,
                            )
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }

            Container(
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.5.dp)
            ) {
                Text(
                    text = "Operating Systems",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 10.dp),
                )

                var selectedOperatingSystem by remember { mutableStateOf<Pie?>(null) }
                var pieChartOperatingSystems by remember(operatingSystems, selectedOperatingSystem) { mutableStateOf(
                    operatingSystems.map { operatingSystem ->
                        Pie(
                            label = operatingSystem.name,
                            data = operatingSystem.totalSeconds,
                            color = colorHash(operatingSystem.name),
                            selected = operatingSystem.name == selectedOperatingSystem?.label
                        )
                    }
                ) }

                val chartSize = 250.dp

                if (pieChartOperatingSystems.isNotEmpty()) {
                    PieChart(
                        data = pieChartOperatingSystems,
                        modifier = Modifier
                            .size(chartSize)
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 20.dp),
                        style = Pie.Style.Fill,
                        selectedScale = 1.2f,
                        onPieClick = { clickedPie ->
                            selectedOperatingSystem = clickedPie
                        },
                    )

                    if (selectedOperatingSystem != null && pieChartOperatingSystems.find { it.label == selectedOperatingSystem?.label } != null) {
                        Row {
                            Box(
                                modifier = Modifier
                                    .size(15.dp)
                                    .background(selectedOperatingSystem?.color ?: Color(0xFFFFFFFF))
                                    .align(Alignment.CenterVertically)
                            )

                            Spacer(
                                modifier = Modifier.width(6.dp)
                            )

                            Text(
                                text = "${selectedOperatingSystem?.label}: ${formatMs(
                                    ms = (selectedOperatingSystem?.data ?: 0.0) * 1000L,
                                    limit = 2
                                )}"
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(chartSize)
                            .shimmerable(
                                enabled = true,
                                shape = CircleShape,
                            )
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }

            Container(
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.5.dp)
            ) {
                Text(
                    text = "Machines",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 10.dp),
                )

                var selectedMachine by remember { mutableStateOf<Pie?>(null) }
                var pieChartMachines by remember(machines, selectedMachine) { mutableStateOf(
                    machines.map { machine ->
                        Pie(
                            label = machine.name,
                            data = machine.totalSeconds,
                            color = colorHash(machine.name),
                            selected = machine.name == selectedMachine?.label
                        )
                    }
                ) }

                val chartSize = 250.dp

                if (pieChartMachines.isNotEmpty()) {
                    PieChart(
                        data = pieChartMachines,
                        modifier = Modifier
                            .size(chartSize)
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 20.dp),
                        style = Pie.Style.Fill,
                        selectedScale = 1.2f,
                        onPieClick = { clickedPie ->
                            selectedMachine = clickedPie
                        },
                    )

                    if (selectedMachine != null && pieChartMachines.find { it.label == selectedMachine?.label } != null) {
                        Row {
                            Box(
                                modifier = Modifier
                                    .size(15.dp)
                                    .background(selectedMachine?.color ?: Color(0xFFFFFFFF))
                                    .align(Alignment.CenterVertically)
                            )

                            Spacer(
                                modifier = Modifier.width(6.dp)
                            )

                            Text(
                                text = "${selectedMachine?.label}: ${formatMs(
                                    ms = (selectedMachine?.data ?: 0.0) * 1000L,
                                    limit = 2
                                )}"
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(chartSize)
                            .shimmerable(
                                enabled = true,
                                shape = CircleShape,
                            )
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
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
                Pie(label = "Android", data = 20.0, color = Color.Red, selectedColor = Color.Green),
                Pie(label = "Windows", data = 45.0, color = Color.Cyan, selectedColor = Color.Blue),
                Pie(label = "Linux", data = 35.0, color = Color.Gray, selectedColor = Color.Yellow),
            )
        )
    }
    PieChart(
        modifier = Modifier.size(200.dp),
        data = data,
        onPieClick = {
            println("${it.label} Clicked")
            val pieIndex = data.indexOf(it)
            data = data.mapIndexed { mapIndex, pie -> pie.copy(selected = pieIndex == mapIndex) }
        },
        selectedScale = 1.2f,
//        scaleAnimEnterSpec = spring<Float>(
//            dampingRatio = Spring.DampingRatioMediumBouncy,
//            stiffness = Spring.StiffnessLow
//        ),
        colorAnimEnterSpec = tween(300),
        colorAnimExitSpec = tween(300),
        scaleAnimExitSpec = tween(300),
        spaceDegreeAnimExitSpec = tween(300),
        style = Pie.Style.Fill
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