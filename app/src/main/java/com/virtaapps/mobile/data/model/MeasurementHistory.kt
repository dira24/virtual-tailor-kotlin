package com.virtaapps.mobile.data.model

import android.os.Parcelable
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentSnapshot
import com.virtaapps.mobile.base.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MeasurementHistory(
    val historyId: String,
    val name: String,
    val address: String,
    val type: String,
    val bahu: String,
    val tangan: String,
    val badan: String,
    val ukuran: String,
    val gambar: String
) : BaseModel(historyId), Parcelable {
    companion object {
        fun DocumentSnapshot.toMeasurementHistory(): MeasurementHistory? {
            return try {
                val name = getString("name")!!
                val address = getString("address")!!
                val type = getString("type")!!
                val bahu = getString("bahu")!!
                val tangan = getString("tangan")!!
                val badan = getString("badan")!!
                val ukuran = getString("ukuran")!!
                val gambar = getString("gambar")!!
                MeasurementHistory(id, name, address, type, bahu, tangan, badan, ukuran, gambar)
            } catch (e: Exception) {
                Log.e(TAG, ERROR_MESSAGE, e)
                FirebaseCrashlytics.getInstance().log(ERROR_MESSAGE)
                FirebaseCrashlytics.getInstance().setCustomKey(TAG, id)
                FirebaseCrashlytics.getInstance().recordException(e)
                null
            }
        }

        private const val TAG = "Measurement History"
        private const val ERROR_MESSAGE = "Error converting measurement history"
    }
}
