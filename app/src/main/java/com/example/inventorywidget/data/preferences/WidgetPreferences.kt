package com.example.inventorywidget.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.inventorywidget.domain.usecase.VerifyUserIsLoggedInUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestor de preferencias del Widget
 * Maneja el estado de visibilidad del saldo (ojo abierto/cerrado)
 */
@Singleton
class WidgetPreferences @Inject constructor(
    @ApplicationContext context: Context,
    private val VerifyUserIsLoggedInUseCase: VerifyUserIsLoggedInUseCase
) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREF_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREF_NAME = "widget_preferences"
        private const val KEY_BALANCE_VISIBLE = "balance_visible"
    }

    fun setBalanceVisible(isVisible: Boolean) {
        prefs.edit().apply {
            putBoolean(KEY_BALANCE_VISIBLE, isVisible)
            apply()
        }
    }

    fun isBalanceVisible(): Boolean {
        return prefs.getBoolean(KEY_BALANCE_VISIBLE, false)
    }

    fun toggleBalanceVisibility(): Boolean {
        val newState = !isBalanceVisible()
        setBalanceVisible(newState)
        return newState
    }
}