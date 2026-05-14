package com.esselunga.navigator.util

import com.esselunga.navigator.data.ShoppingItem
import java.util.prefs.NodeChangeEvent

/**
 * Sorts shopping items into an optimal walking route through the store.
 *
 * Strategy: sort by corsia number ascending (entrance → back).
 * Items with no matched category are appended at the end (ask staff).
 */

data class Node(
    val id: String,
    val type: NodeType,
)

enum class NodeType {
    WALK,
    ZONE,
    ENTRANCE,
    CHECKOUT,
    EXIT
}

data class Edge(
    val from: String,
    val to: String,
    val weight: Int
)

val walkNodes = (1..30).map { i ->
    Node("WALK_$i", NodeType.WALK)
}

val nodes = listOf(
    Node("FRESHPRODUCTS1", NodeType.ZONE),
    Node("FRESHPRODUCTS2", NodeType.ZONE),
    Node("FRESHPRODUCTS3", NodeType.ZONE),
    Node("FRESHPRODUCTS4", NodeType.ZONE),
    Node("FRESHPRODUCTS5", NodeType.ZONE),
    Node("FRESHPRODUCTS6", NodeType.ZONE),
    Node("BAKERY", NodeType.ZONE),
    Node("PASTA_RICE", NodeType.ZONE),
    Node("DISPENSA", NodeType.ZONE),
    Node("DAIRY", NodeType.ZONE),
    Node("DELI", NodeType.ZONE),
    Node("MEAT", NodeType.ZONE),
    Node("FROZEN", NodeType.ZONE),
    Node("BREAKFAST", NodeType.ZONE),
    Node("DRINKS", NodeType.ZONE),
    Node("PERSONAL_CARE", NodeType.ZONE),
    Node("CLEANING", NodeType.ZONE),
    Node("PET", NodeType.ZONE),

    Node("ENTRANCE", NodeType.ENTRANCE),
    Node("EXIT", NodeType.EXIT),

    Node("CHECKOUT1", NodeType.CHECKOUT),
    Node("CHECKOUT2", NodeType.CHECKOUT)
) + walkNodes

val freshEdges = (1 until 6).flatMap { i ->
    listOf(
        Edge("FRESHPRODUCTS$i", "FRESHPRODUCTS${i + 1}", 1),
        Edge("FRESHPRODUCTS${i + 1}", "FRESHPRODUCTS$i", 1) // bidireccional
    )
}

val edges = listOf(

    Edge("ENTRANCE", "WALK_1", 1),

    Edge("WALK_1", "FRESHPRODUCTS1", 0),

    Edge("FRESHPRODUCTS6", "WALK_2", 0),

    Edge("WALK_2", "WALK_3", 1),

    Edge("WALK_3", "CHECKOUT1", 1),
    Edge("WALK_3", "WALK_4", 1),
    Edge("WALK_3", "DELI", 0),

    Edge("WALK_4", "CHECKOUT1", 1),
    Edge("WALK_4", "WALK_5", 1),
    Edge("WALK_4", "DAIRY", 0),

    Edge("WALK_5", "DISPENSA", 0),
    Edge("WALK_5", "CHECKOUT1", 1),
    Edge("WALK_5", "WALK_6", 1),

    Edge("WALK_6", "CHECKOUT1", 1),
    Edge("WALK_6", "WALK_7", 1),
    Edge("WALK_6", "PASTA_RICE", 0),

    Edge("WALK_7", "BREAKFAST", 0),
    Edge("WALK_7", "CHECKOUT2", 1),
    Edge("WALK_7", "WALK_8", 1),

    Edge("WALK_8", "CHECKOUT2", 1),
    Edge("WALK_8", "WALK_9", 1),
    Edge("WALK_8", "PERSONAL_CARE", 0),

    Edge("WALK_9", "CHECKOUT2", 1),
    Edge("WALK_9", "WALK_10", 1),
    Edge("WALK_9", "CLEANING", 0),

    Edge("WALK_10", "WALK_11", 1),
    Edge("WALK_10", "CHECKOUT2", 1),
    Edge("WALK_10", "PET", 0),

    Edge("WALK_11", "CHECKOUT2", 1),
    Edge("WALK_11", "WALK_12", 1),
    Edge("WALK_11", "WALK_18", 1),

    Edge("WALK_12", "WALK_13", 1),
    Edge("WALK_12", "WALK_17", 1),

    Edge("WALK_13", "WALK_14", 1),
    Edge("WALK_13", "FROZEN", 0),

    Edge("WALK_14", "WALK_15", 1),

    Edge("WALK_15", "WALK_16", 1),
    Edge("WALK_15", "WALK_27", 1),

    Edge("WALK_16", "FROZEN", 0),
    Edge("WALK_16", "WALK_17", 1),
    Edge("WALK_16", "WALK_27", 1),

    Edge("WALK_17", "WALK_27", 1),
    Edge("WALK_17", "WALK_18", 1),

    Edge("WALK_18", "WALK_29", 1),
    Edge("WALK_18", "WALK_19", 1),

    Edge("WALK_19", "PET", 0),
    Edge("WALK_19", "WALK_20", 1),
    Edge("WALK_19", "WALK_29", 1),

    Edge("WALK_20", "CLEANING", 0),
    Edge("WALK_20", "WALK_29", 1),
    Edge("WALK_20", "WALK_21", 1),

    Edge("WALK_21", "PERSONAL_CARE", 0),
    Edge("WALK_21", "WALK_22", 1),
    Edge("WALK_21", "WALK_28", 1),

    Edge("WALK_22", "BREAKFAST", 0),
    Edge("WALK_22", "WALK_23", 1),
    Edge("WALK_22", "WALK_28", 1),

    Edge("WALK_23", "PASTA_RICE", 0),
    Edge("WALK_23", "WALK_24", 1),
    Edge("WALK_23", "WALK_28", 1),

    Edge("WALK_24", "DISPENSA", 0),
    Edge("WALK_24", "WALK_25", 1),
    Edge("WALK_24", "WALK_28", 1),

    Edge("WALK_25", "WALK_28", 1),
    Edge("WALK_25", "DAIRY", 0),
    Edge("WALK_25", "WALK_26", 1),

    Edge("WALK_26", "MEAT", 0),
    Edge("WALK_26", "DELI", 0),
    Edge("WALK_26", "WALK_28", 1),

    Edge("WALK_28", "MEAT", 0),
    Edge("WALK_28", "WALK_29", 1),

    Edge("WALK_29", "WALK_27", 1),

    Edge("WALK_27", "BAKERY", 0)

) + freshEdges


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
