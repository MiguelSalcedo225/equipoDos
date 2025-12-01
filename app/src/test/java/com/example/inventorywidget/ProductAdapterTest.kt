package com.example.inventorywidget

import com.example.inventorywidget.model.Product
import com.example.inventorywidget.viewmodel.ProductAdapter
import org.junit.Assert.*
import org.junit.Test

class ProductAdapterTest {

    @Test
    fun `ProductDiffCallback areItemsTheSame devuelve true para mismo código`() {
        // Given
        val product1 = Product(code = 1001, name = "Laptop", unitPrice = 1000.0, quantity = 5)
        val product2 = Product(code = 1001, name = "Laptop Updated", unitPrice = 1200.0, quantity = 3)
        val diffCallback = ProductAdapter.ProductDiffCallback()

        // When
        val result = diffCallback.areItemsTheSame(product1, product2)

        // Then
        assertTrue(result)
    }

    @Test
    fun `ProductDiffCallback areItemsTheSame devuelve false para diferente código`() {
        // Given
        val product1 = Product(code = 1001, name = "Laptop", unitPrice = 1000.0, quantity = 5)
        val product2 = Product(code = 1002, name = "Mouse", unitPrice = 25.0, quantity = 10)
        val diffCallback = ProductAdapter.ProductDiffCallback()

        // When
        val result = diffCallback.areItemsTheSame(product1, product2)

        // Then
        assertFalse(result)
    }

    @Test
    fun `ProductDiffCallback areContentsTheSame devuelve true para productos idénticos`() {
        // Given
        val product1 = Product(code = 1001, name = "Laptop", unitPrice = 1000.0, quantity = 5)
        val product2 = Product(code = 1001, name = "Laptop", unitPrice = 1000.0, quantity = 5)
        val diffCallback = ProductAdapter.ProductDiffCallback()

        // When
        val result = diffCallback.areContentsTheSame(product1, product2)

        // Then
        assertTrue(result)
    }

    @Test
    fun `ProductDiffCallback areContentsTheSame devuelve false para diferente precio`() {
        // Given
        val product1 = Product(code = 1001, name = "Laptop", unitPrice = 1000.0, quantity = 5)
        val product2 = Product(code = 1001, name = "Laptop", unitPrice = 1200.0, quantity = 5)
        val diffCallback = ProductAdapter.ProductDiffCallback()

        // When
        val result = diffCallback.areContentsTheSame(product1, product2)

        // Then
        assertFalse(result)
    }

    @Test
    fun `ProductDiffCallback areContentsTheSame devuelve false para diferente cantidad`() {
        // Given
        val product1 = Product(code = 1001, name = "Laptop", unitPrice = 1000.0, quantity = 5)
        val product2 = Product(code = 1001, name = "Laptop", unitPrice = 1000.0, quantity = 10)
        val diffCallback = ProductAdapter.ProductDiffCallback()

        // When
        val result = diffCallback.areContentsTheSame(product1, product2)

        // Then
        assertFalse(result)
    }

    @Test
    fun `ProductDiffCallback areContentsTheSame devuelve false para diferente nombre`() {
        // Given
        val product1 = Product(code = 1001, name = "Laptop", unitPrice = 1000.0, quantity = 5)
        val product2 = Product(code = 1001, name = "Desktop", unitPrice = 1000.0, quantity = 5)
        val diffCallback = ProductAdapter.ProductDiffCallback()

        // When
        val result = diffCallback.areContentsTheSame(product1, product2)

        // Then
        assertFalse(result)
    }

    @Test
    fun `Product getTotalValue calcula correctamente el valor total`() {
        // Given
        val product = Product(code = 1001, name = "Laptop", unitPrice = 1000.0, quantity = 5)

        // When
        val totalValue = product.getTotalValue()

        // Then
        assertEquals(5000.0, totalValue, 0.01)
    }

    @Test
    fun `Product getTotalValue con cantidad cero devuelve cero`() {
        // Given
        val product = Product(code = 1001, name = "Laptop", unitPrice = 1000.0, quantity = 0)

        // When
        val totalValue = product.getTotalValue()

        // Then
        assertEquals(0.0, totalValue, 0.01)
    }

    @Test
    fun `Product getTotalValue con precio cero devuelve cero`() {
        // Given
        val product = Product(code = 1001, name = "Free Item", unitPrice = 0.0, quantity = 10)

        // When
        val totalValue = product.getTotalValue()

        // Then
        assertEquals(0.0, totalValue, 0.01)
    }
}