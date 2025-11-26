package com.example.inventorywidget.utils

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.example.inventorywidget.view.InventoryWidgetProvider

/**
 * Utilidad para actualizar el widget de Inventory
 */
object WidgetUpdateHelper {

    /**
     * Actualiza todos los widgets de Inventory cuando cambia el inventario
     * @param context contexto de la aplicación
     */
    fun updateWidget(context: Context) {
        val intent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }
        
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val ids = appWidgetManager.getAppWidgetIds(
            ComponentName(context, InventoryWidgetProvider::class.java)
        )
        
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        context.sendBroadcast(intent)
    }
}
