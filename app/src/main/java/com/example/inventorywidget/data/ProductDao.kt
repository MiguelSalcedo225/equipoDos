package com.example.inventorywidget.data

import androidx.room.*
import com.example.inventorywidget.model.Product
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para la entidad Inventory.
 * Permite el acceso a la base de datos Room con soporte para Flows (observación en tiempo real).
 */
@Dao
interface ProductDao {

    /**
     * Inserta un nuevo producto.
     * OnConflictStrategy.ABORT evita duplicados de código.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertProduct(product: Product)

    /**
     * Obtiene todos los productos ordenados por código.
     * Devuelve un Flow para observar los cambios en tiempo real.
     */
    @Query("SELECT * FROM products ORDER BY code ASC")
    fun getAllProducts(): Flow<List<Product>>

    /**
     * Obtiene un producto por su código.
     */
    @Query("SELECT * FROM products WHERE code = :productCode")
    suspend fun getProductByCode(productCode: Int): Product?

    /**
     * Actualiza un producto existente.
     */
    @Update
    suspend fun updateProduct(product: Product)

    /**
     * Elimina un producto.
     */
    @Delete
    suspend fun deleteProduct(product: Product)

    /**
     * Calcula el valor total del inventario (precio * cantidad).
     * Flow permite observar cambios automáticamente cuando se modifica la base de datos.
     */
    @Query("SELECT SUM(unitPrice * quantity) FROM products")
    fun getTotalInventoryValue(): Flow<Double?>

    /**
     * Elimina todos los productos del inventario.
     */
    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()
}
