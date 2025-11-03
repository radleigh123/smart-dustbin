package com.eldroid.trashbincloud.model.repository

import android.util.Log
import com.eldroid.trashbincloud.model.entity.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserRepository(private val database: FirebaseDatabase = FirebaseDatabase.getInstance()) {

    private val usersRef = database.getReference("users")

    // TODO: KotlinDOC
    /**
     * Add a new user
     */
    fun addUser(userUid: String, user: User, callback: (Boolean, String?) -> Unit) {
        usersRef.child(userUid).setValue(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    fun getUser(userUid: String, callback: (User?, String?) -> Unit) {
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

    fun getUsers(callback: (List<User>?, String?) -> Unit) {
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usersList = mutableListOf<User>()
                for (binSnapshot in snapshot.children) {
                    val user = binSnapshot.getValue(User::class.java)
                    user?.let { usersList.add(it) }
                }
                callback(usersList, null)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList(), error.message)
            }
        })
    }

    fun updateUser(
        userUid: String,
        name: String,
        contactNumber: String,
        callback: (Boolean, String?) -> Unit
    ) {
        val updates = mapOf(
            "name" to name,
            "contactNumber" to contactNumber
        )

        usersRef.child(userUid).updateChildren(updates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }


    fun deleteUser(userUid: String, callback: (Boolean, String?) -> Unit) {
        usersRef.child(userUid).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    fun updateFcmToken(userUid: String, token: String) {
        usersRef.child(userUid).child("fcmToken").setValue(token)
    }

    /**
     * For now method is not used
     */
    fun listenForUserUpdates(userUid: String, callback: (User?, String?) -> Unit): ValueEventListener {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                callback(user, null)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, error.message)
            }
        }

        usersRef.child(userUid).addValueEventListener(listener)
        return listener
    }

    /**
     * Remove a listener when no longer needed
     */
    fun removeListener(userUid: String, listener: ValueEventListener) {
        usersRef.child(userUid).removeEventListener(listener)
    }

}