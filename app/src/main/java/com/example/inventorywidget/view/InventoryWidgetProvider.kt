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
import com.example.inventorywidget.domain.usecase.CalculateTotalBalanceUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class InventoryWidgetProvider : AppWidgetProvider() {

    // Inject ONLY what is needed for the Widget UI
    @Inject
    lateinit var calculateTotalBalanceUseCase: CalculateTotalBalanceUseCase

    // Create a Scope that we can control. SupervisorJob ensures a crash in one child doesn't kill others.
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    companion object {
        private const val ACTION_TOGGLE_BALANCE = "com.example.inventorywidget.TOGGLE_BALANCE"
        private const val ACTION_OPEN_APP = "com.example.inventorywidget.OPEN_APP"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        // 1. Handle Navigation cleanly (Don't check logic here, just launch)
        if (intent.action == ACTION_OPEN_APP) {
            val launchIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                // Optional: Add an extra if you want MainActivity to know it came from widget
                putExtra("FROM_WIDGET", true)
            }
            context.startActivity(launchIntent)
            return
        }

        // 2. Handle Background actions using goAsync()
        if (intent.action == ACTION_TOGGLE_BALANCE) {
            // We need to goAsync because accessing Preferences/DataStore is technically I/O
            val pendingResult = goAsync()

            scope.launch {
                try {
                    val widgetPreferences = WidgetPreferences(context)
                    // Assuming toggle is suspend or fast enough.
                    // If it relies on DataStore, it MUST be called within a coroutine.
                    widgetPreferences.toggleBalanceVisibility()

                    // Manually trigger an update after toggling
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    val componentName = ComponentName(context, InventoryWidgetProvider::class.java)
                    val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

                    // We call the update logic manually here
                    updateWidgetsFromBackground(context, appWidgetManager, appWidgetIds)
                } finally {
                    // CRITICAL: Must call finish() to release the WakeLock
                    pendingResult.finish()
                }
            }
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        // Use goAsync() for the standard update loop as well
        // Note: onUpdate calls this, but onUpdate doesn't give us the PendingResult.
        // However, standard onUpdate is usually safe enough for quick UI setup,
        // BUT fetching DB data requires a coroutine scope that survives onUpdate.

        // Strategy: Render the "Loading" or "Static" state immediately (Sync)
        // Then fetch data (Async).

        val views = RemoteViews(context.packageName, R.layout.inventory_widget)
        setupClickListeners(context, views)

        // Show current state immediately? Or launch loader.
        // Ideally, we start a background job to fetch data.

        val pendingResult = goAsync()
        scope.launch {
            try {
                val totalBalance = withContext(Dispatchers.IO) {
                    calculateTotalBalanceUseCase() // Run DB work on IO thread
                }

                val widgetPreferences = WidgetPreferences(context)
                val isBalanceVisible = widgetPreferences.isBalanceVisible() // Suspend function?

                updateViewsWithData(views, totalBalance, isBalanceVisible)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }

    // Helper to reuse logic for the Toggle Action
    private suspend fun updateWidgetsFromBackground(
        context: Context,
        appWidgetManager: AppWidgetManager,
        ids: IntArray
    ) {
        val totalBalance = withContext(Dispatchers.IO) {
            calculateTotalBalanceUseCase()
        }
        val widgetPreferences = WidgetPreferences(context)
        val isBalanceVisible = widgetPreferences.isBalanceVisible()

        for (id in ids) {
            val views = RemoteViews(context.packageName, R.layout.inventory_widget)
            setupClickListeners(context, views)
            updateViewsWithData(views, totalBalance, isBalanceVisible)
            appWidgetManager.updateAppWidget(id, views)
        }
    }

    private fun setupClickListeners(context: Context, views: RemoteViews) {
        // Toggle Intent
        val toggleIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = ACTION_TOGGLE_BALANCE
        }
        val togglePendingIntent = PendingIntent.getBroadcast(
            context, 0, toggleIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_eye_icon, togglePendingIntent)

        // Open App Intent
        val openAppIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = ACTION_OPEN_APP
        }
        val openAppPendingIntent = PendingIntent.getBroadcast(
            context, 1, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_manage_icon, openAppPendingIntent)
    }

    private fun updateViewsWithData(views: RemoteViews, totalBalance: Double, isVisible: Boolean) {
        val balanceText = if (isVisible) formatBalance(totalBalance) else getHiddenBalance(totalBalance)
        views.setTextViewText(R.id.widget_balance, balanceText)

        val eyeIcon = if (isVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed
        views.setImageViewResource(R.id.widget_eye_icon, eyeIcon)
    }

    private fun formatBalance(balance: Double): String {
        val symbols = DecimalFormatSymbols(Locale.US).apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        val formatter = DecimalFormat("#,##0.00", symbols)
        return "$${formatter.format(balance)}"
    }

    private fun getHiddenBalance(balance: Double): String {
        // Logic remains the same
        val formattedBalance = formatBalance(balance)
        val cleanBalance = formattedBalance.replace(Regex("[^0-9]"), "")
        return "$$" + "*".repeat(cleanBalance.length)
    }
}