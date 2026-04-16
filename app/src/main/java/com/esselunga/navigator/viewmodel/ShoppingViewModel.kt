package com.esselunga.navigator.viewmodel

import androidx.lifecycle.ViewModel
import com.esselunga.navigator.data.ShoppingItem
import com.esselunga.navigator.data.findCategory
import com.esselunga.navigator.util.RouteOptimizer
import com.esselunga.navigator.util.RouteStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class ShoppingViewModel : ViewModel() {

    private val _items = MutableStateFlow<List<ShoppingItem>>(emptyList())
    val items: StateFlow<List<ShoppingItem>> = _items.asStateFlow()

    val route: List<RouteStep>
        get() = RouteOptimizer.optimize(_items.value)

    fun addItem(text: String) {
        if (text.isBlank()) return
        val category = findCategory(text)
        _items.update { it + ShoppingItem(rawText = text.trim(), category = category) }
    }

    fun removeItem(id: String) {
        _items.update { it.filter { item -> item.id != id } }
    }

    fun toggleChecked(id: String) {
        _items.update { list ->
            list.map { if (it.id == id) it.copy(checked = !it.checked) else it }
        }
    }

    fun clearAll() {
        _items.update { emptyList() }
    }

    val uncheckedCount: Int get() = _items.value.count { !it.checked }
    val unrecognizedItems: List<ShoppingItem> get() = _items.value.filter { it.category == null }
}
