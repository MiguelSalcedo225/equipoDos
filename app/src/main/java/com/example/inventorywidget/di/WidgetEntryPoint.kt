package com.example.inventorywidget.di

import com.example.inventorywidget.repository.ProductRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * EntryPoint para acceder a las dependencias de Hilt desde el WidgetProvider
 * Los BroadcastReceivers no pueden usar @AndroidEntryPoint directamente,
 * por lo que usamos un EntryPoint para obtener las dependencias.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun productRepository(): ProductRepository
    fun firebaseAuth(): FirebaseAuth
}
