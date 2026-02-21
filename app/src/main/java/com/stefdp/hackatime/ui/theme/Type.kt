package com.stefdp.hackatime.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.stefdp.hackatime.R

val PhantomSans = FontFamily(
    Font(R.font.phantom_sans)
)

fun createTypography(colorScheme: ColorScheme): Typography {
    val defaultTypography = Typography()

    return Typography(
        bodyLarge = defaultTypography.bodyLarge.copy(
            fontFamily = PhantomSans,
            color = colorScheme.onBackground
        ),
        bodyMedium = defaultTypography.bodyMedium.copy(
            fontFamily = PhantomSans,
            color = colorScheme.onBackground
        ),
        bodySmall = defaultTypography.bodySmall.copy(
            fontFamily = PhantomSans,
            color = colorScheme.onBackground
        ),
        titleLarge = defaultTypography.titleLarge.copy(
            fontFamily = PhantomSans,
            color = colorScheme.onBackground
        ),
        titleMedium = defaultTypography.titleMedium.copy(
            fontFamily = PhantomSans,
            color = colorScheme.onBackground
        ),
        titleSmall = defaultTypography.titleSmall.copy(
            fontFamily = PhantomSans,
            color = colorScheme.onBackground
        ),
        labelMedium = defaultTypography.labelMedium.copy(
            fontFamily = PhantomSans,
            color = colorScheme.onBackground
        ),
        labelLarge = defaultTypography.labelLarge.copy(
            fontFamily = PhantomSans,
            color = colorScheme.onBackground
        ), // Seems to be default for Text("")
        labelSmall = defaultTypography.labelSmall.copy(
            fontFamily = PhantomSans,
            color = colorScheme.onBackground
        ),
        displayLarge = defaultTypography.displayLarge.copy(
            fontFamily = PhantomSans,
            color = colorScheme.onBackground
        ),
        displayMedium = defaultTypography.displayMedium.copy(
            fontFamily = PhantomSans,
            color = colorScheme.onBackground
        ),
        displaySmall = defaultTypography.displaySmall.copy(
            fontFamily = PhantomSans,
            color = colorScheme.onBackground
        ),
        headlineLarge = defaultTypography.headlineLarge.copy(
            fontFamily = PhantomSans,
            color = colorScheme.onBackground
        ),
        headlineMedium = defaultTypography.headlineMedium.copy(
            fontFamily = PhantomSans,
            color = colorScheme.onBackground
        ),
        headlineSmall = defaultTypography.headlineSmall.copy(
            fontFamily = PhantomSans,
            color = colorScheme.onBackground
        )
    )
}