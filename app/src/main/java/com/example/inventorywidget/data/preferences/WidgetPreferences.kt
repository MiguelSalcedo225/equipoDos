package com.example.inventorywidget.data.preferences

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestor de preferencias del Widget
 * Maneja la visibilidad del saldo en el widget
 */
@Singleton
class WidgetPreferences @Inject constructor(
    @ApplicationContext context: Context
) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREF_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREF_NAME = "widget_preferences"
        private const val KEY_BALANCE_VISIBLE = "balance_visible"
    }

    /**
     * Verifica si el saldo está visible
     */
    fun isBalanceVisible(): Boolean {
        return prefs.getBoolean(KEY_BALANCE_VISIBLE, false)
    }

    /**
     * Establece la visibilidad del saldo
     */
    fun setBalanceVisible(visible: Boolean) {
        prefs.edit().putBoolean(KEY_BALANCE_VISIBLE, visible).apply()
    }

    /**
     * Alterna la visibilidad del saldo
     */
    fun toggleBalanceVisibility() {
        val currentState = isBalanceVisible()
        setBalanceVisible(!currentState)
    }

    /**
     * Limpia todas las preferencias del widget
     */
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}