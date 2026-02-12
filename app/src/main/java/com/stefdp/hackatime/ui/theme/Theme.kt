package com.stefdp.hackatime.ui.theme

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Text,
    secondary = Secondary,
    onSecondary = Text,
    tertiary = Accent,
    onTertiary = Text,
    background = Background,
    onBackground = Text,
//    surface = Elevated,
    surface = Dark,
    onSurface = Text,
    onSecondaryContainer = Muted,
    outline = Primary,
    outlineVariant = Steel,
    onSurfaceVariant = Muted
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Text,
    secondary = Slate,
    onSecondary = Text,
    tertiary = Accent,
    onTertiary = Darker,
    background = White,
    onBackground = Darker,
    surface = Smoke,
    onSurface = Darker,
    onSecondaryContainer = Darkless,
    outline = Primary,
    outlineVariant = Darkless,
    onSurfaceVariant = Slate
)

@Composable
fun HackatimeStatsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}