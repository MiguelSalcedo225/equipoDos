package com.example.inventorywidget.view

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.inventorywidget.R
import com.example.inventorywidget.data.preferences.WidgetPreferences
import com.example.inventorywidget.repository.ProductRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Widget Provider para Inventory
 * Maneja la actualización y eventos del widget
 * Implementa los criterios 7, 10, 13, 14 para la navegación y autenticación
 */
class InventoryWidgetProvider : AppWidgetProvider() {

    /**
     * EntryPoint para acceder a las dependencias de Hilt desde el Widget
     */
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WidgetProviderEntryPoint {
        fun firebaseAuth(): FirebaseAuth
        fun productRepository(): ProductRepository
        fun widgetPreferences(): WidgetPreferences
    }

    companion object {
        const val ACTION_TOGGLE_BALANCE = "com.example.inventorywidget.TOGGLE_BALANCE"
        const val ACTION_MANAGE_INVENTORY = "com.example.inventorywidget.MANAGE_INVENTORY"
        const val EXTRA_FROM_WIDGET = "from_widget"
        const val EXTRA_WIDGET_ACTION = "widget_action"
        const val ACTION_SHOW_BALANCE = "show_balance"
        const val ACTION_MANAGE = "manage"

        /**
         * Actualiza todos los widgets de la aplicación
         */
        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, InventoryWidgetProvider::class.java)
            )

            val intent = Intent(context, InventoryWidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            }
            context.sendBroadcast(intent)
        }
    }

    /**
     * Obtiene las dependencias desde Hilt EntryPoint
     */
    private fun getEntryPoint(context: Context): WidgetProviderEntryPoint {
        val appContext = context.applicationContext
        return EntryPointAccessors.fromApplication(
            appContext,
            WidgetProviderEntryPoint::class.java
        )
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
                handleToggleBalance(context)
            }
            ACTION_MANAGE_INVENTORY -> {
                handleManageInventory(context)
            }
        }
    }

    /**
     * Criterio 7 y 10: Maneja el clic en el ícono del ojo
     * Si está logueado: muestra/oculta el saldo
     * Si no está logueado: redirige al Login
     */
    private fun handleToggleBalance(context: Context) {
        val entryPoint = getEntryPoint(context)
        val firebaseAuth = entryPoint.firebaseAuth()
        val widgetPreferences = entryPoint.widgetPreferences()

        val isLoggedIn = firebaseAuth.currentUser != null

        if (isLoggedIn) {
            // Usuario logueado: alternar visibilidad del saldo (Criterio 7)
            widgetPreferences.toggleBalanceVisibility()

            // Actualizar todos los widgets
            updateAllWidgets(context)
        } else {
            // Usuario no logueado: redirigir a Login (Criterio 10)
            val loginIntent = Intent(context, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(EXTRA_FROM_WIDGET, true)
                putExtra(EXTRA_WIDGET_ACTION, ACTION_SHOW_BALANCE)
            }
            context.startActivity(loginIntent)
        }
    }

    /**
     * Criterio 13 y 14: Maneja el clic en "Gestionar inventario"
     * Si está logueado: va al Home Inventario
     * Si no está logueado: redirige al Login
     */
    private fun handleManageInventory(context: Context) {
        val entryPoint = getEntryPoint(context)
        val firebaseAuth = entryPoint.firebaseAuth()
        val isLoggedIn = firebaseAuth.currentUser != null

        if (isLoggedIn) {
            // Usuario logueado: ir al Home Inventario (Criterio 14)
            val mainIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(mainIntent)
        } else {
            // Usuario no logueado: redirigir a Login (Criterio 13)
            val loginIntent = Intent(context, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(EXTRA_FROM_WIDGET, true)
                putExtra(EXTRA_WIDGET_ACTION, ACTION_MANAGE)
            }
            context.startActivity(loginIntent)
        }
    }

    override fun onEnabled(context: Context) {
        // Primera instancia del widget creada
        // Resetear preferencias del widget
        val entryPoint = getEntryPoint(context)
        val widgetPreferences = entryPoint.widgetPreferences()
        widgetPreferences.setBalanceVisible(false)
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
        val entryPoint = getEntryPoint(context)
        val widgetPreferences = entryPoint.widgetPreferences()
        val firebaseAuth = entryPoint.firebaseAuth()
        val productRepository = entryPoint.productRepository()

        val isLoggedIn = firebaseAuth.currentUser != null

        // Configurar intent para alternar visibilidad del saldo (Criterio 6, 7)
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

        // Configurar intent para gestionar inventario (Criterio 12, 13, 14)
        val manageIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = ACTION_MANAGE_INVENTORY
        }
        val managePendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            manageIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_manage_container, managePendingIntent)

        // Actualizar UI del widget en segundo plano
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val totalBalance = calculateTotalBalance(productRepository)
                val isBalanceVisible = widgetPreferences.isBalanceVisible() && isLoggedIn

                // Actualizar texto del saldo (Criterio 5, 8, 9)
                val balanceText = if (isBalanceVisible) {
                    formatBalance(totalBalance)
                } else {
                    getHiddenBalance()
                }
                views.setTextViewText(R.id.widget_balance, balanceText)

                // Actualizar ícono del ojo (Criterio 6, 7)
                // Ojo cerrado = saldo oculto, Ojo abierto = saldo visible
                val eyeIcon = if (isBalanceVisible) {
                    R.drawable.ic_eye_open
                } else {
                    R.drawable.ic_eye_closed
                }
                views.setImageViewResource(R.id.widget_eye_icon, eyeIcon)

                // Actualizar el widget
                appWidgetManager.updateAppWidget(appWidgetId, views)
            } catch (e: Exception) {
                // En caso de error, mostrar saldo oculto
                views.setTextViewText(R.id.widget_balance, getHiddenBalance())
                views.setImageViewResource(R.id.widget_eye_icon, R.drawable.ic_eye_closed)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }

    /**
     * Calcula el saldo total del inventario
     */
    private suspend fun calculateTotalBalance(productRepository: ProductRepository): Double {
        return withContext(Dispatchers.IO) {
            try {
                val productList = productRepository.getProductsSnapshot()
                var totalBalance = 0.0
                for (product in productList) {
                    val itemTotal = product.unitPrice * product.quantity
                    totalBalance += itemTotal
                }
                totalBalance
            } catch (e: Exception) {
                0.0
            }
        }
    }

    /**
     * Formatea el saldo con separadores de miles y dos decimales
     * Criterio 9: Ejemplo 3.326.000,00
     */
    private fun formatBalance(balance: Double): String {
        val symbols = DecimalFormatSymbols(Locale.GERMANY).apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }

        val formatter = DecimalFormat("#,##0.00", symbols)
        return "$${formatter.format(balance)}"
    }

    /**
     * Obtiene el saldo formateado oculto
     * Criterio 5: Signo de pesos y 4 asteriscos
     */
    private fun getHiddenBalance(): String {
        return "$****"
    }
}