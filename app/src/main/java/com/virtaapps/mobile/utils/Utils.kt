package com.virtaapps.mobile.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.virtaapps.mobile.data.firebase.FirebaseConstants
import java.text.SimpleDateFormat
import java.util.*

fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}

fun Date?.orNow() = this ?: getCurrentDateTime()

fun String.toDate(format: String = "dd/MM/yyyy", locale: Locale = Locale.getDefault()): Date? =
    SimpleDateFormat(format, locale).parse(this)

fun getCurrentDateTime(): Date {
    return Calendar.getInstance().time
}

fun getTimeLapse(startDate: Date, endDate: Date = getCurrentDateTime(), dayMode: Boolean = false): String {
    val diff: Long = endDate.time - startDate.time
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    val month = days / 30
    val year = month / 12

    if (dayMode) {
        return "$days hari yang lalu"
    }

    return if (year > 0) {
        "$year tahun $month bulan"
    } else {
        "$month bulan"
    }
}

suspend fun <T> safeCallFirebase(
    firebaseCall: suspend () -> T,
    customKey: String = FirebaseConstants.KEY_ID,
    customValue: String = FirebaseAuth.getInstance().currentUser?.uid ?: "",
    customMessage: String = FirebaseConstants.GENERAL_ERROR_MESSAGE
): T? = try {
    firebaseCall.invoke()
} catch (e: Exception) {
    val crashlytics = FirebaseCrashlytics.getInstance()

    Log.e(FirebaseConstants.TAG, customMessage, e)
    crashlytics.log(customMessage)
    crashlytics.setCustomKey(customKey, customValue)
    crashlytics.recordException(e)

    null
}