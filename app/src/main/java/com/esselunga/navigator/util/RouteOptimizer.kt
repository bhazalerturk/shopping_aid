package com.esselunga.navigator.util

import com.esselunga.navigator.data.ShoppingItem
import java.util.prefs.NodeChangeEvent
import com.esselunga.navigator.data.CATEGORIES
import com.esselunga.navigator.data.StoreSection
import android.util.Log

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

    Edge("WALK_3", "CHECKOUT1", 10),
    Edge("WALK_3", "WALK_4", 1),
    Edge("WALK_3", "DELI", 0),

    Edge("WALK_4", "CHECKOUT1", 10),
    Edge("WALK_4", "WALK_5", 1),
    Edge("WALK_4", "DAIRY", 0),

    Edge("WALK_5", "DISPENSA", 0),
    Edge("WALK_5", "CHECKOUT1", 10),
    Edge("WALK_5", "WALK_6", 1),

    Edge("WALK_6", "CHECKOUT1", 10),
    Edge("WALK_6", "WALK_7", 1),
    Edge("WALK_6", "PASTA_RICE", 0),

    Edge("WALK_7", "BREAKFAST", 0),
    Edge("WALK_7", "CHECKOUT2", 10),
    Edge("WALK_7", "WALK_8", 1),

    Edge("WALK_8", "CHECKOUT2", 10),
    Edge("WALK_8", "WALK_9", 1),
    Edge("WALK_8", "PERSONAL_CARE", 0),

    Edge("WALK_9", "CHECKOUT2", 10),
    Edge("WALK_9", "WALK_10", 1),
    Edge("WALK_9", "CLEANING", 0),

    Edge("WALK_10", "WALK_11", 1),
    Edge("WALK_10", "CHECKOUT2", 10),
    Edge("WALK_10", "PET", 0),

    Edge("WALK_11", "CHECKOUT2", 10),
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
    Edge("WALK_25", "MEAT", 0),
    Edge("WALK_24", "MEAT", 0),
    Edge("WALK_23", "MEAT", 0),
    Edge("WALK_22", "MEAT", 0),
    Edge("WALK_21", "MEAT", 0),
    Edge("WALK_20", "MEAT", 0),
    Edge("WALK_19", "MEAT", 0),
    Edge("WALK_18", "MEAT", 0),
    Edge("WALK_17", "MEAT", 0),
    Edge("WALK_16", "MEAT", 0),
    Edge("WALK_15", "MEAT", 0),

    Edge("WALK_26", "DELI", 0),
    Edge("WALK_26", "WALK_28", 1),

    Edge("WALK_28", "MEAT", 0),
    Edge("WALK_28", "WALK_29", 1),

    Edge("WALK_29", "WALK_27", 1),

    Edge("WALK_20", "BAKERY", 0),
    Edge("WALK_19", "BAKERY", 0),
    Edge("WALK_18", "BAKERY", 0),
    Edge("WALK_17", "BAKERY", 0),
    Edge("WALK_16", "BAKERY", 0),
    Edge("WALK_15", "BAKERY", 0),
    Edge("MEAT", "BAKERY", 0)

) + freshEdges

data class OptimizedRoute(
    val path: List<String>,
    val steps: List<RouteStep>
)

object RouteOptimizer {

    fun groupItemsByStoreSection(
        items: List<ShoppingItem>
    ): Map<StoreSection, List<ShoppingItem>> {

        val categoriesById = CATEGORIES.associateBy { it.id }

        return items
            .filter { it.product != null }
            .groupBy { item ->
                val categoryId = item.product!!.categoryId
                val category = categoriesById[categoryId]
                    ?: error("Category not found: $categoryId")
                category.section
            }
    }

    private val orderedSections = listOf(
        StoreSection.FRESHPRODUCTS,
        StoreSection.DELI,
        StoreSection.DAIRY,
        StoreSection.MEAT,
        StoreSection.DISPENSA,
        StoreSection.PASTA_RICE,
        StoreSection.BREAKFAST,
        StoreSection.PERSONAL_CARE,
        StoreSection.CLEANING,
        StoreSection.PET,
        StoreSection.FROZEN
    )

    private val checkout2Sections = setOf(
        StoreSection.BREAKFAST,
        StoreSection.PERSONAL_CARE,
        StoreSection.CLEANING,
        StoreSection.PET,
        StoreSection.FROZEN
    )

    private val bidirectionalEdges = edges.flatMap {
        listOf(
            it,
            Edge(it.to, it.from, it.weight)
        )
    }

    private val graph: Map<String, List<Edge>> =
        bidirectionalEdges.groupBy { it.from }

    private fun shortestPath(
        start: String,
        end: String,
        itemSections: Set<StoreSection>,
        nodePenalties: Map<String, Int> = emptyMap()
    ): List<String> {

        val distances = mutableMapOf<String, Int>()
        val previous  = mutableMapOf<String, String?>()
        val unvisited = nodes.map { it.id }.toMutableSet()

        nodes.forEach {
            distances[it.id] = Int.MAX_VALUE
            previous[it.id]  = null
        }
        distances[start] = 0

        while (unvisited.isNotEmpty()) {

            val current = unvisited.minByOrNull {
                distances[it] ?: Int.MAX_VALUE
            } ?: break

            if (current == end) break
            unvisited.remove(current)

            val neighbors = graph[current] ?: emptyList()

            for (edge in neighbors) {

                val destinationNode = nodes.find { it.id == edge.to }
                val zonePenalty = if (
                    destinationNode?.type == NodeType.ZONE &&
                    nodeIdToSection(edge.to) !in itemSections
                ) 1 else 0

                // Penalització per node ja visitat
                val visitPenalty = nodePenalties[edge.to] ?: 0

                val newDistance = (distances[current] ?: Int.MAX_VALUE) +
                        edge.weight + zonePenalty + visitPenalty

                if (newDistance < (distances[edge.to] ?: Int.MAX_VALUE)) {
                    distances[edge.to] = newDistance
                    previous[edge.to]  = current
                }
            }
        }

        val path = mutableListOf<String>()
        var current: String? = end
        while (current != null) {
            path.add(current)
            current = previous[current]
        }
        return path.reversed()
    }

    private fun nodeIdToSection(nodeId: String): StoreSection? {
        return when {
            nodeId.startsWith("FRESHPRODUCTS") -> StoreSection.FRESHPRODUCTS
            else -> StoreSection.entries.find { it.name == nodeId }
        }
    }

    fun optimize(items: List<ShoppingItem>): OptimizedRoute {

        val grouped = groupItemsByStoreSection(items)
        val requiredSections = grouped.keys
        val itemSections = requiredSections.toSet()

        val visitOrder = orderedSections.filter { it in requiredSections }

        val useCheckout2 = visitOrder.any { it in checkout2Sections }
        val checkout = if (useCheckout2) "CHECKOUT2" else "CHECKOUT1"

        val targets = mutableListOf("ENTRANCE")
        visitOrder.forEach { section ->
            targets.add(
                if (section == StoreSection.FRESHPRODUCTS) "FRESHPRODUCTS1"
                else section.name
            )
        }
        targets.add(checkout)

        Log.d("RouteOptimizer", "========== TARGETS ==========")
        targets.forEach { Log.d("RouteOptimizer", it) }

        val fullRoute = mutableListOf<String>()
        val nodePenalties = mutableMapOf<String, Int>()

        for (i in 0 until targets.size - 1) {

            val from = targets[i]
            val to   = targets[i + 1]

            val segment = shortestPath(from, to, itemSections, nodePenalties)

            Log.d("RouteOptimizer", "PATH: $from -> $to = $segment")

            // Sumar 1000 a tots els nodes intermedis visitats
            segment.drop(1).dropLast(1).forEach { nodeId ->
                nodePenalties[nodeId] = (nodePenalties[nodeId] ?: 0) + 1000
            }

            if (fullRoute.isEmpty()) fullRoute.addAll(segment)
            else fullRoute.addAll(segment.drop(1))
        }

        Log.d("RouteOptimizer", "========== FINAL ROUTE ==========")
        fullRoute.forEach { Log.d("RouteOptimizer", it) }

        val steps = mutableListOf<RouteStep>()
        val visitedSections = mutableSetOf<StoreSection>()

        for (nodeId in fullRoute) {

            val section = nodeIdToSection(nodeId) ?: continue

            if (section in visitedSections) continue
            visitedSections.add(section)

            val sectionItems = grouped[section] ?: emptyList()

            if (sectionItems.isEmpty()) {
                steps.add(RouteStep.PassThrough(section))
            } else {
                steps.add(RouteStep.EnterSection(section))
                sectionItems.forEach { steps.add(RouteStep.PickItem(it)) }
            }
        }

        steps.add(RouteStep.GoToCheckout(checkout))
        steps.add(RouteStep.Finish("EXIT"))

        val unrecognized = items.filter { it.product == null && !it.checked }
        if (unrecognized.isNotEmpty()) {
            steps.add(RouteStep.AskStaff(unrecognized))
        }

        Log.d("RouteOptimizer", "========== ROUTE STEPS ==========")
        steps.forEach {
            when (it) {
                is RouteStep.PassThrough ->
                    Log.d("RouteOptimizer", "PASS THROUGH -> ${it.section.label}")
                is RouteStep.EnterSection ->
                    Log.d("RouteOptimizer", "ENTER SECTION -> ${it.section.label}")
                is RouteStep.PickItem ->
                    Log.d("RouteOptimizer", "PICK ITEM -> ${it.item.product?.name}")
                is RouteStep.GoToCheckout ->
                    Log.d("RouteOptimizer", "GO TO -> ${it.checkoutId}")
                is RouteStep.Finish ->
                    Log.d("RouteOptimizer", "FINISH -> ${it.exitId}")
                is RouteStep.AskStaff ->
                    Log.d("RouteOptimizer", "ASK STAFF -> ${it.items.size} items")
            }
        }

        return OptimizedRoute(
            path = fullRoute,
            steps = steps
        )
    }
}

sealed class RouteStep {
    data class EnterSection(val section: StoreSection) : RouteStep()
    data class PassThrough(val section: StoreSection) : RouteStep()
    data class PickItem(val item: ShoppingItem) : RouteStep()
    data class GoToCheckout(val checkoutId: String) : RouteStep()
    data class Finish(val exitId: String) : RouteStep()
    data class AskStaff(val items: List<ShoppingItem>) : RouteStep()
}