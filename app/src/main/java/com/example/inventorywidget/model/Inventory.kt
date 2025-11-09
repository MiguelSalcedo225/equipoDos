package com.example.inventorywidget.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * Entidad Inventory para Room Database
 * Representa un producto en el inventario.
 */
@Entity(tableName = "inventory")
data class Inventory(
    @PrimaryKey
    val code: Int, // Código del producto (máximo 4 dígitos)
    val name: String, // Nombre del artículo (máximo 40 caracteres)
    val price: Double, // Precio unitario (permite decimales)
    val quantity: Int // Cantidad en inventario
) : Serializable {
    /** Calcula el valor total del producto */
    fun getTotalValue(): Double = price * quantity
}