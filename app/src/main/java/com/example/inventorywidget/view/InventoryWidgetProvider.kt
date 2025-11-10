package com.example.inventorywidget.view

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.inventorywidget.R
import com.example.inventorywidget.data.preferences.WidgetPreferences
import com.example.inventorywidget.viewmodel.WidgetViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Widget Provider para Inventory
 * Maneja la actualización y eventos del widget
 */
class InventoryWidgetProvider : AppWidgetProvider() {

    companion object {
        private const val ACTION_TOGGLE_BALANCE = "com.example.inventorywidget.TOGGLE_BALANCE"
        private const val ACTION_OPEN_APP = "com.example.inventorywidget.OPEN_APP"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Actualizar cada instancia del widget
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            ACTION_TOGGLE_BALANCE -> {
                // Alternar visibilidad del saldo
                val widgetPreferences = WidgetPreferences(context)
                widgetPreferences.toggleBalanceVisibility()

                // Actualizar todos los widgets
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(
                    intent.component
                )
                onUpdate(context, appWidgetManager, appWidgetIds)
            }
            ACTION_OPEN_APP -> {
                // Abrir la aplicación (MainActivity que redirige a Login)
                val launchIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(launchIntent)
            }
        }
    }

    override fun onEnabled(context: Context) {
        // Primera instancia del widget creada
    }

    override fun onDisabled(context: Context) {
        // Última instancia del widget eliminada
    }

    /**
     * Actualiza el contenido del widget
     */
    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.inventory_widget)
        val widgetPreferences = WidgetPreferences(context)
        val widgetViewModel = WidgetViewModel(context.applicationContext as android.app.Application)

        // Configurar intent para alternar visibilidad del saldo
        val toggleBalanceIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = ACTION_TOGGLE_BALANCE
        }
        val toggleBalancePendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            toggleBalanceIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_eye_icon, toggleBalancePendingIntent)

        // Configurar intent para abrir la app
        val openAppIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = ACTION_OPEN_APP
        }
        val openAppPendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_manage_container, openAppPendingIntent)

        // Actualizar UI del widget en segundo plano
        CoroutineScope(Dispatchers.Main).launch {
            val totalBalance = widgetViewModel.calculateTotalBalance()
            val isBalanceVisible = widgetPreferences.isBalanceVisible()

            // Actualizar texto del saldo
            val balanceText = if (isBalanceVisible) {
                widgetViewModel.formatBalance(totalBalance)
            } else {
                widgetViewModel.getHiddenBalance()
            }
            views.setTextViewText(R.id.widget_balance, balanceText)

            // Actualizar ícono del ojo
            val eyeIcon = if (isBalanceVisible) {
                R.drawable.ic_eye_open
            } else {
                R.drawable.ic_eye_closed
            }
            views.setImageViewResource(R.id.widget_eye_icon, eyeIcon)

            // Actualizar el widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
