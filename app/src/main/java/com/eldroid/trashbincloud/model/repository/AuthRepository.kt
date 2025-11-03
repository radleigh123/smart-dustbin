package com.eldroid.trashbincloud.model.repository

import com.eldroid.trashbincloud.model.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class AuthRepository(private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
                     private val userRepository: UserRepository = UserRepository(FirebaseDatabase.getInstance())
) {



    fun currentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    fun register(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {

                        val newUser = User(
                            uid = user.uid,
                            email = email,
                            displayName = email.substringBefore("@"),
                        )

                        userRepository.addUserInfo(newUser)
                    }
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    fun sendResetPasswordEmail(email: String, callback: (Boolean, String?) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    fun logout() {
        auth.signOut()
    }
}