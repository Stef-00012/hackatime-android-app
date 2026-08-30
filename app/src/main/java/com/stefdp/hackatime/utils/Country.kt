package com.stefdp.hackatime.utils

import java.util.Locale

fun String.toCountryName(displayLocale: Locale = Locale.getDefault()): String {
    if (this.length != 2) return this

    val countryLocale = Locale.Builder()
        .setRegion(this.uppercase())
        .build()

    return countryLocale.getDisplayCountry(displayLocale)
}

fun String.toFlagEmoji(): String {
    if (this.length != 2) return this

    val upper = this.uppercase()
    val firstChar = Character.codePointAt(upper, 0) - 0x41 + 0x1F1E6
    val secondChar = Character.codePointAt(upper, 1) - 0x41 + 0x1F1E6

    return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
}