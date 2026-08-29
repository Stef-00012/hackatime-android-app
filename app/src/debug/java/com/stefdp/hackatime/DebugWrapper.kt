package com.stefdp.hackatime

import android.util.Log
import androidx.compose.runtime.Composable

const val IS_DEBUG = true
const val DEBUG_NETWORK = false

@Composable
fun DebugWrapper(content: @Composable () -> Unit) {
    content()
}

class Logger {
    companion object {
        fun debug(tag: String, message: String, throwable: Throwable? = null) {
            Log.d(tag, message, throwable)
        }

        fun error(tag: String, message: String, throwable: Throwable? = null) {
            Log.e(tag, message, throwable)
        }

        fun info(tag: String, message: String, throwable: Throwable? = null) {
            Log.i(tag, message, throwable)
        }

        fun warn(tag: String, message: String, throwable: Throwable? = null) {
            Log.w(tag, message, throwable)
        }

        fun verbose(tag: String, message: String, throwable: Throwable? = null) {
            Log.v(tag, message, throwable)
        }

        fun wtf(tag: String, message: String, throwable: Throwable? = null) {
            Log.wtf(tag, message, throwable)
        }
    }
}