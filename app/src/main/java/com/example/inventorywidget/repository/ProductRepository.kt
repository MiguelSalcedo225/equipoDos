package com.example.inventorywidget.repository

import android.content.Context
import com.example.inventorywidget.data.AppDatabase
import com.example.inventorywidget.data.ProductDao
import com.example.inventorywidget.model.Product
import kotlinx.coroutines.flow.Flow

/**
 * Repository pattern para abstraer el acceso a datos.
 * Actúa como intermediario entre el ViewModel y el DAO.
 * Permite la observación de cambios en la base de datos mediante Flow.
 */
class ProductRepository(context: Context) {

    private val productDao: ProductDao = AppDatabase.getDatabase(context).productDao()

    /**
     * Obtiene todos los productos como Flow para observar cambios en tiempo real.
     */
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()

    /**
     * Obtiene el valor total del inventario (como Flow).
     */
    val totalInventoryValue: Flow<Double?> = productDao.getTotalInventoryValue()

    /**
     * Obtiene un producto por su código.
     */
    suspend fun getProductByCode(code: Int): Product? {
        return productDao.getProductByCode(code)
    }

    /**
     * Inserta un nuevo producto (puede lanzar excepción si el código ya existe).
     */
    suspend fun insertProduct(product: Product) {
        productDao.insertProduct(product)
    }

    /**
     * Actualiza un producto existente.
     */
    suspend fun updateProduct(product: Product) {
        productDao.updateProduct(product)
    }

    /**
     * Elimina un producto.
     */
    suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product)
    }

    /**
     * Elimina todos los productos (útil para pruebas o reinicio total).
     */
    suspend fun deleteAllProducts() {
        productDao.deleteAllProducts()
    }


}
