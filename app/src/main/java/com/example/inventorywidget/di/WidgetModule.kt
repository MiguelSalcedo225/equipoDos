package com.example.inventorywidget.di

import com.example.inventorywidget.view.WidgetUpdateHandler
import com.example.inventorywidget.view.WidgetUpdateHandlerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WidgetModule {

    @Binds
    @Singleton
    abstract fun bindWidgetUpdateHandler(
        impl: WidgetUpdateHandlerImpl
    ): WidgetUpdateHandler
}
