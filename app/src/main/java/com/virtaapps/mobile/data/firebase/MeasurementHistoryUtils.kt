package com.virtaapps.mobile.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.virtaapps.mobile.data.firebase.FirebaseConstants.HISTORY_COLLECTION
import com.virtaapps.mobile.data.firebase.FirebaseConstants.USER_COLLECTION
import com.virtaapps.mobile.data.model.MeasurementHistory
import com.virtaapps.mobile.data.model.MeasurementHistory.Companion.toMeasurementHistory
import com.virtaapps.mobile.utils.safeCallFirebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MeasurementHistoryUtils @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) {

    private fun getUserId() = auth.currentUser?.uid ?: ""

    suspend fun uploadImage(data: ByteArray): String? = safeCallFirebase(
        firebaseCall = {
            val ref = storage.getReference(HISTORY_COLLECTION).child(System.currentTimeMillis().toString())
            ref.putBytes(data).await().storage.downloadUrl.await().toString()
        }
    )

    suspend fun saveHistory(result: MeasurementHistory) = safeCallFirebase(
        firebaseCall = {
            db.collection(USER_COLLECTION).document(getUserId()).collection(HISTORY_COLLECTION)
                .add(result).await().get().await().toMeasurementHistory()
        }
    )

    suspend fun getHistories() = safeCallFirebase(
        firebaseCall = { db.collection(USER_COLLECTION).document(getUserId()).collection(HISTORY_COLLECTION)
            .get().await().mapNotNull { it.toMeasurementHistory() } },
        customMessage = "Error getting user histories"
    )
}