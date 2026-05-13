package com.esselunga.navigator.util

import com.esselunga.navigator.data.ShoppingItem

/**
 * Sorts shopping items into an optimal walking route through the store.
 *
 * Strategy: sort by corsia number ascending (entrance → back).
 * Items with no matched category are appended at the end (ask staff).
 */
object RouteOptimizer {

    fun optimize(items: List<ShoppingItem>): List<RouteStep> {
        val recognized = items
            .filter { it.product != null && !it.checked }
            .sortedBy { null}

        val unrecognized = items.filter { it.product == null && !it.checked }

        val steps = mutableListOf<RouteStep>()
        var lastCorsia = -1

        for (item in recognized) {
            val corsia = null
            /*
            if (corsia != lastCorsia) {
                steps.add(RouteStep.GoToAisle(null, item.category.section.label))
                lastCorsia = null
            }

             */
            steps.add(RouteStep.PickItem(item))
        }

        if (unrecognized.isNotEmpty()) {
            steps.add(RouteStep.AskStaff(unrecognized))
        }

        return steps
    }
}

sealed class RouteStep {
    data class GoToAisle(val corsia: Int, val sectionLabel: String) : RouteStep()
    data class PickItem(val item: ShoppingItem) : RouteStep()
    data class AskStaff(val items: List<ShoppingItem>) : RouteStep()
}
