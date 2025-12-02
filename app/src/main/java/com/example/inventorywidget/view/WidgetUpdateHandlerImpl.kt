package com.example.inventorywidget.view

import android.app.Application
import javax.inject.Inject

class WidgetUpdateHandlerImpl @Inject constructor() : WidgetUpdateHandler {

    override fun update(app: Application) {
        InventoryWidgetProvider.updateAllWidgets(app)
    }
}
