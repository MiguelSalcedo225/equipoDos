package com.example.inventorywidget.repository

import com.example.inventorywidget.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow


/**
 * Repository pattern para abstraer el acceso a datos.
 * Actúa como intermediario entre el ViewModel y el DAO.
 * Permite la observación de cambios en la base de datos mediante Flow.
 */

class ProductRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {


    private val productsCollection = firestore.collection("products")

    suspend fun getProductsSnapshot(): List<Product> {
        return try {
            val snapshot = productsCollection.get().await()
            snapshot.toObjects(Product::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

     fun allProducts(): Flow<List<Product>> = callbackFlow {
        // 1. Create the listener
        val subscription = productsCollection.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                // Close the flow with the error
                close(exception)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                // Convert documents to objects and send them to the Flow
                val products = snapshot.toObjects(Product::class.java)
                trySend(products)
            }
        }

        // 2. Cleanup when the Flow is cancelled (screen closed)
        awaitClose { subscription.remove() }
    }

    /**
     * Inserta un nuevo producto (puede lanzar excepción si el código ya existe).
     */
    suspend fun insertProduct(product: Product) {
        // In Firestore, we usually use a String ID.
        // If your product code is unique (like "1234"), use it as the document ID.
        val documentId = product.code.toString()

        try {
            // .set() creates or overwrites the document with that ID
            productsCollection.document(documentId).set(product).await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getProductByCode(code:Int):Product? {
        // In Firestore, we usually use a String ID.
        // If your product code is unique (like "1234"), use it as the document ID.
        val documentId = code.toString()

        return try {
            // .set() creates or overwrites the document with that ID
           val snapshot= productsCollection.document(documentId).get().await()
            snapshot.toObject(Product::class.java)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Actualiza un producto existente.
     */
    suspend fun updateProduct(product: Product) {
        val documentId = product.code.toString()

        try {

            productsCollection.document(product.code.toString()).set(product).await()
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Elimina un producto.
     */
    suspend fun deleteProduct(product: Product) {
        try {
            productsCollection.document(product.code.toString()).delete().await()
        } catch (e: Exception){
            throw e
        }
    }




}
