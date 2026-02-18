package com.stefdp.hackatime.utils

import androidx.compose.ui.text.AnnotatedString

fun CharSequence.toAnnotatedString(): AnnotatedString {
    return if (this is AnnotatedString) this else AnnotatedString(this.toString())
}