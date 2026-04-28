package com.esselunga.navigator.util

import com.esselunga.navigator.data.ShoppingItem

enum class BudgetStatus { OK, WARNING, OVER, NOT_SET }

object BudgetCalculator {
    fun totalCost(items: List<ShoppingItem>): Double =
        items.sumOf { it.priceEuro * it.quantity }

    fun budgetStatus(total: Double, budget: Double): BudgetStatus {
        if (budget <= 0) return BudgetStatus.NOT_SET
        val ratio = total / budget
        return when {
            ratio >= 1.0 -> BudgetStatus.OVER
            ratio >= 0.7 -> BudgetStatus.WARNING
            else -> BudgetStatus.OK
        }
    }

    fun formatEuro(amount: Double): String = "€%.2f".format(amount)

    fun budgetProgress(total: Double, budget: Double): Float {
        if (budget <= 0) return 0f
        return (total / budget).toFloat().coerceAtMost(1f)
    }
}
