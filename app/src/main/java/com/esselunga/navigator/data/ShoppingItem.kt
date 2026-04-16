package com.esselunga.navigator.data

import java.util.UUID

data class ShoppingItem(
    val id: String = UUID.randomUUID().toString(),
    val rawText: String,                        // what the user typed
    val category: ProductCategory? = null,      // matched category (null = unrecognized)
    val checked: Boolean = false
)
