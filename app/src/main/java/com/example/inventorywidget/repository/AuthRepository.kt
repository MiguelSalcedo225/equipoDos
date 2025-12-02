package com.example.inventorywidget.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.example.inventorywidget.utils.Resource
import com.example.inventorywidget.model.User
import com.example.inventorywidget.data.preferences.SessionManager
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val sessionManager: SessionManager
) {

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    suspend fun login(email: String, password: String): Resource<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                //Guardamos la sesión en SharedPreferences
                sessionManager.setLoggedIn(
                    isLoggedIn = true,
                    userName = user.email ?: ""
                )

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
            Resource.Error("Login incorrecto")
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
                //Guardamos la sesión en SharedPreferences
                sessionManager.setLoggedIn(
                    isLoggedIn = true,
                    userName = user.email ?: ""
                )

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
        //Limpiamos la sesión de SharedPreferences
        sessionManager.logout()
    }

    //Verificamos SOLO con SharedPreferences
    fun isUserLoggedIn(): Boolean {
        return sessionManager.isLoggedIn()
    }
}