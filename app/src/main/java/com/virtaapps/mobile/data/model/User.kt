package com.virtaapps.mobile.data.model

import android.os.Parcelable
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val userId: String,
    val name: String,
    val address: String) : Parcelable {

    companion object {
        fun DocumentSnapshot.toUser(): User? {
            return try {
                val name = getString("name")!!
                val address = getString("address")!!
                User(id, name, address)
            } catch (e: Exception) {
                Log.e(TAG, ERROR_MESSAGE, e)
                FirebaseCrashlytics.getInstance().log(ERROR_MESSAGE)
                FirebaseCrashlytics.getInstance().setCustomKey(TAG, id)
                FirebaseCrashlytics.getInstance().recordException(e)
                null
            }
        }

        fun getDefaultUser(name: String, address: String) = hashMapOf(
            "name" to name,
            "address" to address
        )

        private const val TAG = "User"
        private const val ERROR_MESSAGE = "Error converting user profile"
    }
}
