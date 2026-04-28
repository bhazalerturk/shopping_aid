package com.esselunga.navigator.data

import java.util.UUID

data class ShoppingItem(
    val id: String = UUID.randomUUID().toString(),
    val rawText: String,
    val category: ProductCategory? = null,
    val checked: Boolean = false,
    val priceEuro: Double = 0.0,
    val quantity: Int = 1
) {
    val totalPrice: Double get() = priceEuro * quantity
}
