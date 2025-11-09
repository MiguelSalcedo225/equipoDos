package com.example.inventorywidget.repository

import android.content.Context
import com.example.inventorywidget.data.InventoryDB
import com.example.inventorywidget.data.InventoryDao
import com.example.inventorywidget.model.Inventory
import kotlinx.coroutines.flow.Flow

/**
 * Repository pattern para abstraer el acceso a datos.
 * Conecta el ViewModel con la capa de Room (DAO).
 */
class InventoryRepository(context: Context) {

    private val inventoryDao: InventoryDao = InventoryDB.getDatabase(context).inventoryDao()

    /** Obtiene todos los productos */
    suspend fun getAllInventory(): List<Inventory> =
        inventoryDao.getAllInventory()

    /** Inserta un nuevo producto (puede lanzar excepción si el código ya existe) */
    suspend fun insertInventory(inventory: Inventory) =
        inventoryDao.insertInventory(inventory)

    /** Obtiene un producto por su código */
    suspend fun getInventoryByCode(code: Int): Inventory? =
        inventoryDao.getInventoryByCode(code)

    /** Actualiza un producto existente */
    suspend fun updateInventory(inventory: Inventory) =
        inventoryDao.updateInventory(inventory)

    /** Elimina un producto */
    suspend fun deleteInventory(inventory: Inventory) =
        inventoryDao.deleteInventory(inventory)

    /** (Opcional) Observa el valor total del inventario */
    fun getTotalInventoryValue(): Flow<Double?> =
        inventoryDao.getTotalInventoryValue()
}