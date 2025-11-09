package com.example.inventorywidget.data

import androidx.room.*
import com.example.inventorywidget.model.Inventory
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {

    /** Inserta un nuevo producto.
     *  OnConflictStrategy.ABORT evita duplicados de código. */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertInventory(inventory: Inventory)

    /** Obtiene todos los productos. */
    @Query("SELECT * FROM inventory ORDER BY code ASC")
    suspend fun getAllInventory(): List<Inventory>

    /** Obtiene un producto por su código. */
    @Query("SELECT * FROM inventory WHERE code = :productCode")
    suspend fun getInventoryByCode(productCode: Int): Inventory?

    /** Actualiza un producto existente. */
    @Update
    suspend fun updateInventory(inventory: Inventory)

    /** Elimina un producto. */
    @Delete
    suspend fun deleteInventory(inventory: Inventory)

    /** (Opcional) Calcula el valor total del inventario. */
    @Query("SELECT SUM(price * quantity) FROM inventory")
    fun getTotalInventoryValue(): Flow<Double?>
}
