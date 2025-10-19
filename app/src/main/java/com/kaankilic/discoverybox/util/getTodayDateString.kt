package com.kaankilic.discoverybox.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getTodayDateString(): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(Date())
}
