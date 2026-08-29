package com.stefdp.hackatime

import androidx.compose.runtime.Composable

const val IS_DEBUG = false
const val DEBUG_NETWORK = false

@Composable
fun DebugWrapper(content: @Composable () -> Unit) {}

class Logger {
    companion object {
        fun debug(tag: String, message: String, throwable: Throwable? = null) {}

        fun error(tag: String, message: String, throwable: Throwable? = null) {}

        fun info(tag: String, message: String, throwable: Throwable? = null) {}

        fun warn(tag: String, message: String, throwable: Throwable? = null) {}

        fun verbose(tag: String, message: String, throwable: Throwable? = null) {}

        fun wtf(tag: String, message: String, throwable: Throwable? = null) {}
    }
}