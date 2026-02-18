package com.stefdp.hackatime.screens.home

import android.content.Context
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.google.gson.annotations.SerializedName
import com.stefdp.hackatime.LocalLoggedUser
import com.stefdp.hackatime.R
import com.stefdp.hackatime.components.Popup
import com.stefdp.hackatime.network.hackatimeapi.models.EditorLast7Days
import com.stefdp.hackatime.network.hackatimeapi.models.Feature
import com.stefdp.hackatime.network.hackatimeapi.models.MachineLast7Days
import com.stefdp.hackatime.network.hackatimeapi.models.OperatingSystemLast7Days
import com.stefdp.hackatime.network.hackatimeapi.requests.getCurrentUserStats
import com.stefdp.hackatime.network.hackatimeapi.requests.getCurrentUserStatsLast7Days
import com.stefdp.hackatime.screens.LoginScreen
import com.stefdp.hackatime.screens.home.components.Container
import com.stefdp.hackatime.utils.DayData
import com.stefdp.hackatime.utils.GeneralStat
import com.stefdp.hackatime.utils.colorHash
import com.stefdp.hackatime.utils.formatMs
import com.stefdp.hackatime.utils.getLast7DaysData
import com.stefdp.hackatime.utils.getTop
import com.stefdp.hackatime.utils.languageColors
import com.stefdp.hackatime.utils.shimmerable
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.IndicatorCount
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
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
import kotlin.time.Duration.Companion.days

val pieChartSize = 250.dp

@Composable
fun HomeScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity
) {
    val localUserStats = LocalLoggedUser.current
    val scrollState = rememberScrollState()

    if (localUserStats == null) {
        navController.navigate(LoginScreen) {
            popUpTo(navController.graph.id) { inclusive = true }
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        var showRangePopup by rememberSaveable { mutableStateOf(false) }
        val rangeText = when (statsRange) {
            Range.LAST_SEVEN_DAYS -> stringResource(R.string.date_range_last_7_days)
            Range.ALL_TIME -> stringResource(R.string.date_range_all_time)
            Range.CUSTOM -> "$rangeStart - $rangeEnd"
            Range.ONE_DAY -> rangeStart
        }

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
            )
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
                    statsRange = Range.LAST_SEVEN_DAYS
                    showRangePopup = false
                }
            ) {
                Text(
                    text = stringResource(R.string.date_range_last_7_days_button)
                )
            }

            Button(
                modifier = Modifier.fillMaxWidth(1f).padding(start = 5.dp, end = 5.dp, top = 8.dp, bottom = 0.dp),
                onClick = {
                    statsRange = Range.ALL_TIME
                    showRangePopup = false
                }
            ) {
                Text(
                    text = stringResource(R.string.date_range_all_time_button)
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

        Container(
            modifier = Modifier.padding(
                start = 5.dp,
                end = 5.dp,
                top = 5.dp,
                bottom = 2.5.dp
            )
        ) {
            Text(
                text = stringResource(R.string.total_time),
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
                text = stringResource(R.string.top_project),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = topProject?.name ?: stringResource(R.string.unknown_project),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        }

        Container(
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.5.dp)
        ) {
            Text(
                text = stringResource(R.string.top_language),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = topLanguage?.name ?: stringResource(R.string.unknown_language),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (statsRange == Range.LAST_SEVEN_DAYS) {
            Container(
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.5.dp)
            ) {
                Text(
                    text = stringResource(R.string.top_operating_system),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = topOperatingSystem?.name ?: stringResource(R.string.unknown_operating_system),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Container(
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.5.dp)
            ) {
                Text(
                    text = stringResource(R.string.top_editor),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = topEditor?.name ?: stringResource(R.string.unknown_editor),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Container(
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.5.dp)
            ) {
                Text(
                    text = stringResource(R.string.top_machine),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = topMachine?.name ?: stringResource(R.string.unknown_machine),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Container(
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.5.dp)
            ) {
                Text(
                    text = stringResource(R.string.last_7_days_overview),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                val primaryColor = MaterialTheme.colorScheme.primary
                val chartHeight = 250.dp

                if (last7DaysData.isNotEmpty()) {
                    val lines by remember(last7DaysData) { mutableStateOf(
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
                                1 -> stringResource(R.string.last_7_days_chart_monday)
                                2 -> stringResource(R.string.last_7_days_chart_tuesday)
                                3 -> stringResource(R.string.last_7_days_chart_wednesday)
                                4 -> stringResource(R.string.last_7_days_chart_thursday)
                                5 -> stringResource(R.string.last_7_days_chart_friday)
                                6 -> stringResource(R.string.last_7_days_chart_saturday)
                                7 -> stringResource(R.string.last_7_days_chart_sunday)
                                else -> stringResource(R.string.last_7_days_chart_unknown)
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

        @Composable
        fun PieChartSkeleton() {
            Box(
                modifier = Modifier
                    .size(pieChartSize)
                    .shimmerable(
                        enabled = true,
                        shape = CircleShape,
                    )
                    .align(Alignment.CenterHorizontally)
            )
        }

        @Composable
        fun PieChartTooltip() {
            Row {
                Icon(
                    painter = painterResource(R.drawable.info),
                    contentDescription = stringResource(R.string.pie_chart_tooltip_content_description),
                    modifier = Modifier.size(15.dp).align(Alignment.CenterVertically),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Text(
                    text = stringResource(R.string.pie_chart_tooltip),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Container(
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.5.dp)
        ) {
            Text(
                text = stringResource(R.string.languages_pie_chart),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 10.dp),
            )

            var selectedLanguage by remember { mutableStateOf<Pie?>(null) }
            var pieChartLanguages by remember(languages, selectedLanguage) { mutableStateOf(
                languages
                    .groupBy { it.name }
                    .map { (name, language) ->
                        Pie(
                            label = name,
                            data = language.sumOf { it.totalSeconds },
                            color = languageColors[name.lowercase()] ?: colorHash(name),
                            selected = name == selectedLanguage?.label
                        )
                    }
            ) }

            LaunchedEffect(statsRange, rangeStart, rangeEnd) {
                selectedLanguage = null
            }

            if (pieChartLanguages.isNotEmpty()) {
                PieChart(
                    data = pieChartLanguages,
                    modifier = Modifier
                        .size(pieChartSize)
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 20.dp),
                    style = Pie.Style.Fill,
                    selectedScale = 1.2f,
                    onPieClick = { clickedPie ->
                        selectedLanguage = clickedPie
                    },
                    labelHelperProperties = LabelHelperProperties(
                        enabled = false
                    )
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
                } else {
                    PieChartTooltip()
                }
            } else {
                PieChartSkeleton()

                PieChartTooltip()
            }
        }

        if (statsRange == Range.LAST_SEVEN_DAYS) {
            Container(
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.5.dp)
            ) {
                Text(
                    text = stringResource(R.string.editors_pie_chart),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 10.dp),
                )

                var selectedEditor by remember { mutableStateOf<Pie?>(null) }
                var pieChartEditors by remember(editors, selectedEditor) { mutableStateOf(
                    editors
                        .groupBy { it.name }
                        .map { (name, editor) ->
                            Pie(
                                label = name,
                                data = editor.sumOf { it.totalSeconds },
                                color = colorHash(name),
                                selected = name == selectedEditor?.label
                            )
                        }
                ) }

                LaunchedEffect(statsRange, rangeStart, rangeEnd) {
                    selectedEditor = null
                }

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
                        labelHelperProperties = LabelHelperProperties(
                            enabled = false
                        )
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
                    } else {
                        PieChartTooltip()
                    }
                } else {
                    PieChartSkeleton()

                    PieChartTooltip()
                }
            }

            Container(
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.5.dp)
            ) {
                Text(
                    text = stringResource(R.string.operating_systems_pie_chart),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 10.dp),
                )

                var selectedOperatingSystem by remember { mutableStateOf<Pie?>(null) }
                var pieChartOperatingSystems by remember(operatingSystems, selectedOperatingSystem) { mutableStateOf(
                    operatingSystems
                        .groupBy { it.name }
                        .map { (name, operatingSystem) ->
                            Pie(
                                label = name,
                                data = operatingSystem.sumOf { it.totalSeconds },
                                color = colorHash(name),
                                selected = name == selectedOperatingSystem?.label
                            )
                        }
                ) }

                LaunchedEffect(statsRange, rangeStart, rangeEnd) {
                    selectedOperatingSystem = null
                }

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
                        labelHelperProperties = LabelHelperProperties(
                            enabled = false
                        )
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
                    } else {
                        PieChartTooltip()
                    }
                } else {
                    PieChartSkeleton()

                    PieChartTooltip()
                }
            }

            Container(
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.5.dp)
            ) {
                Text(
                    text = stringResource(R.string.machines_pie_chart),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 10.dp),
                )

                var selectedMachine by remember { mutableStateOf<Pie?>(null) }
                var pieChartMachines by remember(machines, selectedMachine) { mutableStateOf(
                    machines
                        .groupBy { it.name }
                        .map { (name, machine) ->
                            Pie(
                                label = name,
                                data = machine.sumOf { it.totalSeconds },
                                color = colorHash(name),
                                selected = name == selectedMachine?.label
                            )
                        }
                ) }

//                    .groupBy { it.name }
//                    .map { (name, language) ->
//                        Pie(
//                            label = name,
//                            data = language.sumOf { it.totalSeconds },
//                            color = languageColors[name.lowercase()] ?: colorHash(name),
//                            selected = name == selectedLanguage?.label
//                        )
//                    }

                LaunchedEffect(statsRange, rangeStart, rangeEnd) {
                    selectedMachine = null
                }

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
                        labelHelperProperties = LabelHelperProperties(
                            enabled = false
                        )
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
                    } else {
                        PieChartTooltip()
                    }
                } else {
                    PieChartSkeleton()

                    PieChartTooltip()
                }
            }
        }
    }
}

private enum class Range(val value: String) {
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