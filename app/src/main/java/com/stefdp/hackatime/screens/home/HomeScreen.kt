package com.stefdp.hackatime.screens.home

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.UserHandle
import android.util.Log
import android.view.Display
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.stefdp.hackatime.LocalLoggedUser
import com.stefdp.hackatime.network.hackatimeapi.models.responses.UserStats
import com.stefdp.hackatime.screens.LoginScreen
import com.stefdp.hackatime.screens.SettingsScreen
import com.stefdp.hackatime.screens.home.components.Container
import com.stefdp.hackatime.ui.theme.HackatimeStatsTheme
import com.stefdp.hackatime.utils.formatMs
import com.stefdp.hackatime.utils.getTop
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.Pie

// TODO("Apparently home is inserted twice in the navigation stack")

@Composable
fun HomeScreen(
    navController: NavHostController,
    context: Context
) {
    var localUserStats = LocalLoggedUser.current
    var scrollState = rememberScrollState()

    if (localUserStats == null) {
        navController.navigate(LoginScreen) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
        }
    }

    val userStats = localUserStats ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
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
                text = formatMs(userStats.totalSeconds * 1000L),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        }

        Container(
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.5.dp)
        ) {
            val topProject = getTop(userStats.projects)

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
            val topLanguage = getTop(userStats.languages)

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

        // TODO("Add a range button and it range is set to 'Last 7 days' also show machines, editors and OS's")
    }
}

//@Preview
@Composable
fun TestChart() {
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

