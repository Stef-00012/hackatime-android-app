package com.stefdp.hackatime.widgets.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.createBitmap

data class CircularProgressBarOptions(
    val size: Int,
    val barWidth: Float,
    val backgroundColor: Color,
    val progressColor: Color,
    val showPercentage: Boolean = false,
    val fontSize: Float = 0f,
    val textColor: Color? = null,
    val textBold: Boolean = true
)

fun createCircularProgressBar(
    context: Context,
    progress: Float,
    options: CircularProgressBarOptions
): Bitmap {
    val clampedProgress = progress.coerceIn(0f, 100f)

    val density = context.resources.displayMetrics.density

    val sizePx = (options.size * density).toInt()
    val barWidthPx = options.barWidth * density
    val fontSizePx = options.fontSize * density

    val bitmap = createBitmap(sizePx, sizePx)
    val canvas = Canvas(bitmap)

    val center = sizePx / 2f
    val radius = (sizePx - barWidthPx) / 2f

    val resolvedBgColor = options.backgroundColor.toArgb()
    val resolvedProgressColor = options.progressColor.toArgb()

    val backgroundPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = barWidthPx
        color = resolvedBgColor
    }
    canvas.drawCircle(center, center, radius, backgroundPaint)

    val progressPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = barWidthPx
        color = resolvedProgressColor
        strokeCap = Paint.Cap.ROUND
    }

    val oval = RectF(
        center - radius,
        center - radius,
        center + radius,
        center + radius
    )

    val sweepAngle = (clampedProgress / 100f) * 360f

    canvas.drawArc(oval, -90f, sweepAngle, false, progressPaint)

    if (options.showPercentage && options.textColor != null) {
        val resolvedTextColor = options.textColor.toArgb()

        val textPaint = Paint().apply {
            isAntiAlias = true
            color = resolvedTextColor
            textSize = fontSizePx
            textAlign = Paint.Align.CENTER
            typeface = if (options.textBold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        }

        val textHeight = textPaint.descent() - textPaint.ascent()
        val textOffset = (textHeight / 2) - textPaint.descent()
        val textY = center + textOffset

        val text = "${clampedProgress.toInt()}%"
        canvas.drawText(text, center, textY, textPaint)
    }

    bitmap.density = context.resources.displayMetrics.densityDpi

    return bitmap
}