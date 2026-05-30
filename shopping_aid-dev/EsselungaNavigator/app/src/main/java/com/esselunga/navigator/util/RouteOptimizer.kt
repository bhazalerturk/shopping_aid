package com.esselunga.navigator.util

import com.esselunga.navigator.data.ShoppingItem

object RouteOptimizer {

    fun optimize(items: List<ShoppingItem>): List<RouteStep> {
        val recognized = items
            .filter { it.category != null && !it.checked }
            .sortedBy { it.category!!.corsia }

        val unrecognized = items.filter { it.category == null && !it.checked }

        val steps = mutableListOf<RouteStep>()
        var lastCorsia = -1

        for (item in recognized) {
            val corsia = item.category!!.corsia
            if (corsia != lastCorsia) {
                steps.add(RouteStep.GoToAisle(corsia, item.category.section.label))
                lastCorsia = corsia
            }
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
