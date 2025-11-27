package com.example.inventorywidget.repository

import com.example.inventorywidget.model.User
import com.example.inventorywidget.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    suspend fun login(email: String, password: String): Resource<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                Resource.Success(
                    User(
                        uid = user.uid,
                        email = user.email ?: "",
                        displayName = user.displayName
                    )
                )
            } else {
                Resource.Error("Login incorrecto")
            }
        } catch (e: Exception) {
            Resource.Error( "Login incorrecto")
        }
    }

    suspend fun register(
        email: String,
        password: String,
        displayName: String
    ): Resource<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                // Update profile with display name
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
                user.updateProfile(profileUpdates).await()

                Resource.Success(
                    User(
                        uid = user.uid,
                        email = user.email ?: "",
                        displayName = displayName
                    )
                )
            } else {
                Resource.Error("Registration failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}