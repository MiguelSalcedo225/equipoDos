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
import com.example.inventorywidget.domain.usecase.VerifyUserIsLoggedInUseCase
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
class InventoryWidgetProvider : AppWidgetProvider() { // 1. REMOVE constructor arguments

    // 2. USE FIELD INJECTION
    @Inject
    lateinit var calculateTotalBalanceUseCase: CalculateTotalBalanceUseCase

    @Inject
    lateinit var verifyUserIsLoggedInUseCase: VerifyUserIsLoggedInUseCase

    @Inject
    lateinit var widgetPreferences: WidgetPreferences

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
        // Hilt injects dependencies here automatically before super.onReceive
        super.onReceive(context, intent)

        if (intent.action == ACTION_OPEN_APP) {
            val launchIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("FROM_WIDGET", true)
            }
            context.startActivity(launchIntent)
            return
        }

        if (intent.action == ACTION_TOGGLE_BALANCE) {
            val pendingResult = goAsync()
            scope.launch {
                try {
                    widgetPreferences.toggleBalanceVisibility()

                    // Manually update
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    val componentName = ComponentName(context, InventoryWidgetProvider::class.java)
                    val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

                    updateWidgetsFromBackground(context, appWidgetManager, appWidgetIds)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }

    // ... (Keep updateAppWidget and updateWidgetsFromBackground logic the same) ...
    // Note: Ensure your 'setupClickListeners' is actually called inside updateAppWidget

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.inventory_widget)

        // CRITICAL: Setup listeners BEFORE launching the coroutine
        setupClickListeners(context, views)

        val pendingResult = goAsync()
        scope.launch {
            try {
                // ... logic to fetch data ...
                val totalBalance = withContext(Dispatchers.IO) { calculateTotalBalanceUseCase() }
                val isBalanceVisible = widgetPreferences.isBalanceVisible() && verifyUserIsLoggedInUseCase()

                updateViewsWithData(views, totalBalance, isBalanceVisible)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun setupClickListeners(context: Context, views: RemoteViews) {
        // Toggle
        val toggleIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = ACTION_TOGGLE_BALANCE
        }
        // REQUEST CODE MUST BE UNIQUE (0 here)
        val togglePendingIntent = PendingIntent.getBroadcast(
            context, 0, toggleIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_eye_icon, togglePendingIntent)

        // Open App
        val openAppIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = ACTION_OPEN_APP
        }
        // REQUEST CODE MUST BE UNIQUE (1 here)
        val openAppPendingIntent = PendingIntent.getBroadcast(
            context, 1, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_manage_icon, openAppPendingIntent)
    }

    // ... Keep helper functions (updateViewsWithData, formatters) ...
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
        val formattedBalance = formatBalance(balance)
        val cleanBalance = formattedBalance.replace(Regex("[^0-9]"), "")
        return "$$" + "*".repeat(cleanBalance.length)
    }

    // ... Copy your updateWidgetsFromBackground here ...
    private suspend fun updateWidgetsFromBackground(
        context: Context,
        appWidgetManager: AppWidgetManager,
        ids: IntArray
    ) {
        val totalBalance = withContext(Dispatchers.IO) {
            calculateTotalBalanceUseCase()
        }
        val isBalanceVisible = widgetPreferences.isBalanceVisible() && verifyUserIsLoggedInUseCase()

        for (id in ids) {
            val views = RemoteViews(context.packageName, R.layout.inventory_widget)
            setupClickListeners(context, views) // Don't forget this line!
            updateViewsWithData(views, totalBalance, isBalanceVisible)
            appWidgetManager.updateAppWidget(id, views)
        }
    }
}