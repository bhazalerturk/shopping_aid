package com.esselunga.navigator.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.esselunga.navigator.data.*
import com.esselunga.navigator.util.BudgetCalculator
import com.esselunga.navigator.util.RouteOptimizer
import com.esselunga.navigator.util.RouteStep
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*

class ShoppingViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("easylunga", Context.MODE_PRIVATE)

    // ── Shopping List ─────────────────────────────────────────────────────────
    private val _items = MutableStateFlow<List<ShoppingItem>>(emptyList())
    val items: StateFlow<List<ShoppingItem>> = _items.asStateFlow()

    val route: List<RouteStep>
        get() = RouteOptimizer.optimize(_items.value)

    fun addItem(text: String) {
        if (text.isBlank()) return
        val category = findCategory(text)
        val price = category?.defaultPrice ?: 0.0
        val qty = if (category != null) getSuggestedQuantity(category) else 1
        _items.update {
            it + ShoppingItem(rawText = text.trim(), category = category, priceEuro = price, quantity = qty)
        }
    }

    fun getSuggestedQuantity(category: ProductCategory): Int {
        val days = _wizardDays.value
        val people = _wizardPeople.value
        // Only suggest if wizard was actually used (not defaults)
        if (days == 1 && people == 1) return 1
        return (category.suggestedPerDay * days * people).toInt().coerceAtLeast(1)
    }

    fun addItemWithQuantity(categoryId: String, quantity: Int) {
        val category = ALL_CATEGORIES.find { it.id == categoryId } ?: return
        val existing = _items.value.find { it.category?.id == categoryId }
        if (existing != null) {
            _items.update { list ->
                list.map { if (it.id == existing.id) it.copy(quantity = it.quantity + quantity) else it }
            }
        } else {
            _items.update {
                it + ShoppingItem(
                    rawText = category.displayNameEn,
                    category = category,
                    priceEuro = category.defaultPrice,
                    quantity = quantity
                )
            }
        }
    }

    fun removeItem(id: String) {
        _items.update { it.filter { item -> item.id != id } }
    }

    fun toggleChecked(id: String) {
        _items.update { list ->
            list.map { if (it.id == id) it.copy(checked = !it.checked) else it }
        }
    }

    fun incrementQuantity(id: String) {
        _items.update { list ->
            list.map { if (it.id == id) it.copy(quantity = (it.quantity + 1).coerceAtMost(20)) else it }
        }
    }

    fun decrementQuantity(id: String) {
        _items.update { list ->
            list.map { if (it.id == id && it.quantity > 1) it.copy(quantity = it.quantity - 1) else it }
        }
    }

    fun clearAll() {
        _items.update { emptyList() }
    }

    val uncheckedCount: Int get() = _items.value.count { !it.checked }
    val unrecognizedItems: List<ShoppingItem> get() = _items.value.filter { it.category == null }

    // Reactive total cost — always in sync with items list
    val totalCostFlow: StateFlow<Double> = _items
        .map { list -> list.sumOf { it.priceEuro * it.quantity } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    val totalCost: Double get() = totalCostFlow.value

    // ── Budget ────────────────────────────────────────────────────────────────
    private val _budget = MutableStateFlow(0.0)
    val budget: StateFlow<Double> = _budget.asStateFlow()

    fun setBudget(amount: Double) {
        _budget.value = amount
    }

    val budgetRemaining: Double get() = (_budget.value - totalCost).coerceAtLeast(0.0)
    val budgetProgress: Float get() = BudgetCalculator.budgetProgress(totalCost, _budget.value)

    /** Returns true if adding this item cost would push total over budget. */
    fun wouldExceedBudget(itemCost: Double): Boolean {
        if (_budget.value <= 0) return false
        return totalCost + itemCost > _budget.value
    }

    fun canAfford(price: Double): Boolean {
        if (_budget.value <= 0) return true
        return totalCost + price <= _budget.value
    }

    // ── Wizard ────────────────────────────────────────────────────────────────
    private val _wizardDays = MutableStateFlow(1)
    val wizardDays: StateFlow<Int> = _wizardDays.asStateFlow()

    private val _wizardPeople = MutableStateFlow(1)
    val wizardPeople: StateFlow<Int> = _wizardPeople.asStateFlow()

    fun setWizardDays(days: Int) { _wizardDays.value = days }
    fun setWizardPeople(people: Int) { _wizardPeople.value = people }

    fun validateList(): List<String> {
        val warnings = mutableListOf<String>()
        for (item in _items.value) {
            if (item.quantity > 8) {
                warnings.add("You have ${item.quantity} x ${item.rawText} — are you sure?")
            }
        }
        if (_budget.value > 0 && totalCost > _budget.value) {
            warnings.add("Your list costs ${BudgetCalculator.formatEuro(totalCost)}, which is over your budget of ${BudgetCalculator.formatEuro(_budget.value)}.")
        }
        return warnings
    }

    // ── Caregiver ─────────────────────────────────────────────────────────────
    private val _caregiver = MutableStateFlow(loadCaregiver())
    val caregiver: StateFlow<CaregiverContact?> = _caregiver.asStateFlow()

    fun setCaregiverContact(name: String, phone: String) {
        val contact = CaregiverContact(name.trim(), phone.trim())
        _caregiver.value = contact
        prefs.edit()
            .putString("caregiver_name", contact.name)
            .putString("caregiver_phone", contact.phoneNumber)
            .apply()
    }

    fun clearCaregiverContact() {
        _caregiver.value = null
        prefs.edit().remove("caregiver_name").remove("caregiver_phone").apply()
    }

    fun buildShareText(): String {
        val sb = StringBuilder("🛒 My Easylunga shopping list:\n\n")
        for (item in _items.value) {
            sb.append("• ${item.rawText} x${item.quantity} — ${BudgetCalculator.formatEuro(item.totalPrice)}\n")
        }
        sb.append("\nTotal: ${BudgetCalculator.formatEuro(totalCost)}")
        if (_budget.value > 0) sb.append(" / ${BudgetCalculator.formatEuro(_budget.value)}")
        return sb.toString()
    }

    fun shareSmsIntent(): Intent {
        val contact = _caregiver.value
        val uri = if (contact != null) Uri.parse("smsto:${contact.phoneNumber}") else Uri.parse("smsto:")
        return Intent(Intent.ACTION_SENDTO, uri).apply {
            putExtra("sms_body", buildShareText())
        }
    }

    fun callCaregiverIntent(): Intent? {
        val phone = _caregiver.value?.phoneNumber ?: return null
        return Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
    }

    private fun loadCaregiver(): CaregiverContact? {
        val name = prefs.getString("caregiver_name", null) ?: return null
        val phone = prefs.getString("caregiver_phone", null) ?: return null
        return CaregiverContact(name, phone)
    }
}
