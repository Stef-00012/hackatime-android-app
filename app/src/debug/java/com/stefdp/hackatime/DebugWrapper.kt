package com.stefdp.hackatime

import androidx.compose.runtime.Composable

@Composable
fun DebugWrapper(content: @Composable () -> Unit) {
    content()
}