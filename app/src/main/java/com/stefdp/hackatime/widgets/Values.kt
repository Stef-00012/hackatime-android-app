package com.stefdp.hackatime.widgets

import androidx.compose.ui.unit.dp

//const val CELL_WIDTH = 90
const val CELL_WIDTH = 91
//const val CELL_HEIGHT = 100
const val CELL_HEIGHT = 126
val cornerRadius = 16.dp

data class WidgetConfigSize(
    val width: Int,
    val height: Int,
    val label: String,
    val default: Boolean = false
)