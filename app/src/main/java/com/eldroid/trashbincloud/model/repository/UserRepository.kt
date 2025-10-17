package com.eldroid.trashbincloud.model.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

/**
 * TEMP: For a more richer metadata, Firestore is need
 */
class UserRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val usersCollection = firestore.collection("users")

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    /**
     * Add bin
     */
    fun registerBin(binId: String, callback: (Boolean, String?) -> Unit) {
        val userId = getCurrentUserId() ?: return callback(false, "User not authenticated")

        usersCollection.document(userId)
            .update("bins", FieldValue.arrayUnion(binId))
            .addOnSuccessListener {
                callback(true, null)
            }
            .addOnFailureListener { e ->
                callback(false, e.message)
            }
    }

}