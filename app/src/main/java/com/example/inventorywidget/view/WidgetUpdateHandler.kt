package com.example.inventorywidget.view

import android.app.Application

interface WidgetUpdateHandler {
    fun update(app: Application)
}