package com.eldroid.trashbincloud.model.repository

import android.util.Log
import com.eldroid.trashbincloud.model.entity.User
import com.google.firebase.database.FirebaseDatabase

class UserRepository(
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
) {
    private val usersRef = database.getReference("users")

    fun getUserInfo(userUid: String, callback: (User?, String?) -> Unit) {
        usersRef.child(userUid).get().addOnSuccessListener { snapshot ->
            Log.i("UserRepository", "getUserInfo:success")
            val user = snapshot.getValue(User::class.java)
            if (user != null) {
                callback(user, null)
            } else {
                callback(null, "User not found")
            }
        }.addOnFailureListener { e ->
            Log.e("UserRepository", "getUserInfo:failed", e)
            callback(null, e.message)
        }
    }


    fun addUserInfo(user: User) {
        if (user.uid == null) {
            Log.e("UserRepository", "addUserInfo: failed, uid is null")
            return
        }

        usersRef.child(user.uid!!).setValue(user)
            .addOnSuccessListener {
                Log.i("UserRepository", "addUserInfo:success")
            }
            .addOnFailureListener { e ->
                Log.e("UserRepository", "addUserInfo:failed", e)
            }
    }

    fun updateUserInfo(userUid: String, newDisplayName: String, callback: (Boolean, String?) -> Unit) {
        usersRef.child(userUid).child("displayName").setValue(newDisplayName)
            .addOnSuccessListener {
                Log.i("UserRepository", "updateUserInfo:success")
                callback(true, null)
            }
            .addOnFailureListener { e ->
                Log.e("UserRepository", "updateUserInfo:failed", e)
                callback(false, e.message)
            }
    }
}
