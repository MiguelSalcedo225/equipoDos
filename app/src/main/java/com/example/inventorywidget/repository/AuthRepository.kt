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
                        email = user.email ?: ""

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
        password: String

    ): Resource<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {


                Resource.Success(
                    User(
                        uid = user.uid,
                        email = user.email ?: ""

                    )
                )
            } else {
                Resource.Error("Error en el registro")
            }
        } catch (e: Exception) {
            Resource.Error("Error en el registro")
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}