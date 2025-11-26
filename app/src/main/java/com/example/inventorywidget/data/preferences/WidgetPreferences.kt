package com.example.inventorywidget.data.preferences

import android.content.Context
import android.content.SharedPreferences

/**
 * Gestor de preferencias del Widget
 * Maneja el estado de visibilidad del saldo (ojo abierto/cerrado)
 */
class WidgetPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREF_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREF_NAME = "widget_preferences"
        private const val KEY_BALANCE_VISIBLE = "balance_visible"
    }

    /**
     * Guarda el estado de visibilidad del saldo
     * @param isVisible true si el saldo está visible, false si está oculto
     */
    fun setBalanceVisible(isVisible: Boolean) {
        prefs.edit().apply {
            putBoolean(KEY_BALANCE_VISIBLE, isVisible)
            apply()
        }
    }

    /**
     * Verifica si el saldo está visible
     * @return true si está visible (ojo abierto), false si está oculto (ojo cerrado)
     * Por defecto inicia oculto (false)
     */
    fun isBalanceVisible(): Boolean {
        return prefs.getBoolean(KEY_BALANCE_VISIBLE, false)
    }

    /**
     * Alterna el estado de visibilidad del saldo
     * @return nuevo estado de visibilidad
     */
    fun toggleBalanceVisibility(): Boolean {
        val newState = !isBalanceVisible()
        setBalanceVisible(newState)
        return newState
    }
}
