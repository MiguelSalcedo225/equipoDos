package com.example.inventorywidget.data.preferences

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestor de sesión de usuario usando SharedPreferences
 * Maneja el estado de autenticación del usuario
 * Inyectado con Dagger Hilt
 */
@Singleton
class SessionManager @Inject constructor(
    private val prefs: SharedPreferences
) {

    companion object {
        const val KEY_IS_LOGGED_IN = "is_logged_in"
        const val KEY_USER_NAME = "user_name"
        const val KEY_LAST_LOGIN = "last_login"
    }

    /**
     * Guarda el estado de inicio de sesión del usuario
     */
    fun setLoggedIn(isLoggedIn: Boolean, userName: String = "") {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
            putString(KEY_USER_NAME, userName)
            putLong(KEY_LAST_LOGIN, System.currentTimeMillis())
            apply()
        }
    }

    /**
     * Verifica si el usuario está logueado
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Obtiene el nombre del usuario
     */
    fun getUserName(): String? {
        return prefs.getString(KEY_USER_NAME, null)
    }

    /**
     * Obtiene la fecha del último login
     */
    fun getLastLogin(): Long {
        return prefs.getLong(KEY_LAST_LOGIN, 0L)
    }

    /**
     * Cierra la sesión del usuario
     */
    fun logout() {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, false)
            remove(KEY_USER_NAME)
            apply()
        }
    }

    /**
     * Limpia todas las preferencias
     */
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}