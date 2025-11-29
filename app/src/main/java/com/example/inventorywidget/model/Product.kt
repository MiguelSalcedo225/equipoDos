package com.example.inventorywidget.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * Entidad Inventory para Room Database
 * Representa un producto en el inventario.
 */
@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    val code: Int=0, // Código del producto (máximo 4 dígitos)
    val name: String="", // Nombre del artículo (máximo 40 caracteres)
    val unitPrice: Double=0.0, // Precio unitario (permite decimales)
    val quantity: Int=0 // Cantidad en inventario
) : Serializable {
    /** Calcula el valor total del producto */
    fun getTotalValue(): Double = unitPrice * quantity
}