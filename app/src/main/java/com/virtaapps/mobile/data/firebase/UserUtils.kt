package com.virtaapps.mobile.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.virtaapps.mobile.data.firebase.FirebaseConstants.KEY_EMAIL
import com.virtaapps.mobile.data.firebase.FirebaseConstants.KEY_USER_ID
import com.virtaapps.mobile.data.firebase.FirebaseConstants.USER_COLLECTION
import com.virtaapps.mobile.data.model.User
import com.virtaapps.mobile.data.model.User.Companion.toUser
import com.virtaapps.mobile.utils.safeCallFirebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserUtils @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {
    fun getCurrentUser() = auth.currentUser

    fun getUserId() = auth.currentUser?.uid ?: ""

    fun getUserEmail() = auth.currentUser?.email ?: ""

    fun isLoggedIn() = getCurrentUser() != null

    suspend fun login(email: String, password: String) = safeCallFirebase(
        firebaseCall = { auth.signInWithEmailAndPassword(email, password).await().user },
        customKey = KEY_EMAIL,
        customValue = email,
        customMessage = "Error signing in user"
    )

    suspend fun register(name: String, address: String, email: String, password: String) = safeCallFirebase(
        firebaseCall = {
            val data = User.getDefaultUser(name, address)
            val user = auth.createUserWithEmailAndPassword(email, password).await().user
            db.collection(USER_COLLECTION).document(user?.uid ?: "").set(data).await()
        },
        customKey = KEY_EMAIL,
        customValue = email,
        customMessage = "Error signing up user"
    )

    suspend fun logout() = safeCallFirebase(
        firebaseCall = { auth.signOut() }
    )

    suspend fun getProfile() = safeCallFirebase(
        firebaseCall = { db.collection(USER_COLLECTION).document(getUserId()).get().await().toUser() },
        customKey = KEY_USER_ID,
        customMessage = "Error getting user details"
    )
}