package com.example.inventorywidget.repository

import com.example.inventorywidget.model.Product
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

/**
 * Repository pattern to abstract data access.
 * Acts as an intermediary between the ViewModel and Firestore.
 * * SCHEMA STRUCTURE (Option 1):
 * Root Collection: "products"
 * -> Document: {userId} (Phantom document acting as folder)
 * -> Subcollection: "products"
 * -> Document: {productCode} (The actual data)
 */

class ProductRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository
) {

    /**
     * Helper property to dynamically generate the path for the CURRENT USER.
     * Path: db.collection("products").document(USER_ID).collection("products")
     * Returns null if no user is logged in.
     */
    private val currentUserSubCollection: CollectionReference?
        get() {
            val user = authRepository.currentUser
            return if (user != null) {
                firestore.collection("products") // Root collection name as requested
                    .document(user.uid)          // Isolate by User ID
                    .collection("products")      // User's private subcollection
            } else {
                null
            }
        }

    /**
     * Gets a one-time list of products strictly for the current user.
     */
    suspend fun getProductsSnapshot(): List<Product> {
        return try {
            val collection = currentUserSubCollection ?: return emptyList()

            val snapshot = collection.get().await()
            snapshot.toObjects(Product::class.java)
        } catch (e: Exception) {
            // In a snapshot, we might prefer returning empty list on error rather than crashing
            emptyList()
        }
    }

    /**
     * Observes the current user's products in real-time.
     * Wrapped in try-catch to handle initialization errors safely.
     */
    fun allProducts(): Flow<List<Product>> = callbackFlow {
        try {
            val collection = currentUserSubCollection

            // If no user is logged in, close immediately with an exception
            if (collection == null) {
                close(Exception("User not logged in"))
                return@callbackFlow
            }

            // 1. Create the listener on the USER'S subcollection
            val subscription = collection.addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val products = snapshot.toObjects(Product::class.java)
                    trySend(products)
                }
            }

            // 2. Cleanup when the Flow is cancelled (screen closed)
            awaitClose { subscription.remove() }

        } catch (e: Exception) {
            close(e) // Close the flow if any synchronous setup code fails
        }
    }

    /**
     * Inserts a new product into the current user's subcollection.
     * Uses product.code as the Document ID.
     */
    suspend fun insertProduct(product: Product) {
        try {
            val collection = currentUserSubCollection
                ?: throw Exception("User is not logged in")

            // Path: products/{uid}/products/{code}
            collection.document(product.code.toString())
                .set(product)
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Gets a specific product by code from the current user's inventory.
     */
    suspend fun getProductByCode(code: Int): Product? {
        return try {
            val collection = currentUserSubCollection ?: return null
            val documentId = code.toString()

            val snapshot = collection.document(documentId).get().await()
            snapshot.toObject(Product::class.java)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Updates an existing product in the current user's inventory.
     */
    suspend fun updateProduct(product: Product) {
        try {
            val collection = currentUserSubCollection
                ?: throw Exception("User not logged in")

            // Overwrites the document at products/{uid}/products/{code}
            collection.document(product.code.toString())
                .set(product)
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Deletes a product from the current user's inventory.
     */
    suspend fun deleteProduct(product: Product) {
        try {
            val collection = currentUserSubCollection
                ?: throw Exception("User not logged in")

            collection.document(product.code.toString())
                .delete()
                .await()
        } catch (e: Exception) {
            throw e
        }
    }
}