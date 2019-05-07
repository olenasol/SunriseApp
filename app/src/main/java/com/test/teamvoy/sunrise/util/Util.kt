package com.test.teamvoy.sunrise.util

import java.text.SimpleDateFormat
import java.util.*

fun getTimeStringFromUtcString(utcString: String?):String{
    val df = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
    df.timeZone = TimeZone.getTimeZone("UTC")
    val inputMillis = df.parse(utcString).time
    df.timeZone = TimeZone.getDefault()
    val string = df.format(Date(inputMillis))
    //Local time on phone
    return "UTC:$utcString\nLTP:$string"
}