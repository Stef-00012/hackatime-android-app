package com.stefdp.hackatime.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.glance.GlanceTheme
import androidx.glance.material3.ColorProviders

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Text,
    secondary = Secondary,
    onSecondary = Text,
    tertiary = Accent,
    onTertiary = Text,
    background = Background,
    onBackground = Text,
    surface = Dark,
    surfaceVariant = Elevated,
    onSurface = Text,
    onSecondaryContainer = Muted,
    outline = Primary,
    outlineVariant = Steel,
    onSurfaceVariant = Muted
)

val DarkWidgetBackground = Background
val DarkWidgetSurface = Dark

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
    surfaceVariant = ElevatedLight,
    onSurface = Darker,
    onSecondaryContainer = Darkless,
    outline = Primary,
    outlineVariant = Darkless,
    onSurfaceVariant = Slate
)

val LightWidgetBackground = White
val LightWidgetSurface = Smoke

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
        typography = createTypography(colorScheme),
        content = content
    )
}

@Composable
fun HackatimeStatsWidgetTheme(
    content: @Composable () -> Unit
) {
    val colorProviders = ColorProviders(
        light = LightColorScheme,
        dark = DarkColorScheme
    )

    GlanceTheme(
        colors = colorProviders,
        content = content
    )
}