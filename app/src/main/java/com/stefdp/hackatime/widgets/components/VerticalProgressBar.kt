package com.stefdp.hackatime.widgets.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment as GlanceAlignment
import androidx.glance.layout.Box as GlanceBox
import androidx.glance.layout.height
import androidx.glance.layout.width
import androidx.glance.unit.ColorProvider

@Composable
fun GlanceVerticalProgressBar(
    progress: Float,
    progressColor: ColorProvider,
    trackColor: ColorProvider,
    modifier: GlanceModifier = GlanceModifier,
    width: Dp = 16.dp,
    totalHeight: Dp = 120.dp
) {
    val safeProgress = progress.coerceIn(0f, 1f)

    val progressHeight = (totalHeight.value * safeProgress).dp

    GlanceBox(
        modifier = modifier
            .width(width)
            .height(totalHeight)
            .background(trackColor)
            .cornerRadius(999.dp),
        contentAlignment = GlanceAlignment.BottomCenter
    ) {
        if (safeProgress > 0f) {
            GlanceBox(
                modifier = GlanceModifier
                    .width(width)
                    .height(progressHeight)
                    .background(progressColor)
                    .cornerRadius(999.dp)
            ) {}
        }
    }
}

@Composable
fun VerticalProgressBar(
    progress: Float,
    progressColor: Color,
    trackColor: Color,
    modifier: Modifier = Modifier,
    width: Dp = 16.dp,
    totalHeight: Dp = 120.dp
) {
    val safeProgress = progress.coerceIn(0f, 1f)

    val progressHeight = (totalHeight.value * safeProgress).dp

    Box(
        modifier = modifier
            .width(width)
            .clip(RoundedCornerShape(999.dp))
            .height(totalHeight)
            .background(trackColor),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (safeProgress > 0f) {
            Box(
                modifier = Modifier
                    .width(width)
                    .clip(RoundedCornerShape(999.dp))
                    .height(progressHeight)
                    .background(progressColor)
            ) {}
        }
    }
}