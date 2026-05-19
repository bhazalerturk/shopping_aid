package com.esselunga.navigator.util

import com.esselunga.navigator.data.ShoppingItem
import com.esselunga.navigator.data.CATEGORIES
import com.esselunga.navigator.data.StoreSection
import android.util.Log
import kotlin.math.sqrt

// ── Coordenades normalitzades de cada node ─────────────────────────────────

private val nodeCoordinates: Map<String, Pair<Float, Float>> = mapOf(
    "ENTRANCE"       to Pair(0.0701f, 0.2107f),
    "EXIT"           to Pair(0.0335f, 0.7502f),
    "CHECKOUT1"      to Pair(0.1151f, 0.4193f),
    "CHECKOUT2"      to Pair(0.1137f, 0.6119f),
    "FRESHPRODUCTS1" to Pair(0.2038f, 0.2476f),
    "FRESHPRODUCTS2" to Pair(0.2038f, 0.2013f),
    "FRESHPRODUCTS3" to Pair(0.4305f, 0.2013f),
    "FRESHPRODUCTS4" to Pair(0.6374f, 0.2013f),
    "FRESHPRODUCTS5" to Pair(0.6374f, 0.2592f),
    "FRESHPRODUCTS6" to Pair(0.4276f, 0.2592f),
    "WALK_1"         to Pair(0.1250f, 0.2469f),
    "WALK_2"         to Pair(0.3024f, 0.2592f),
    "WALK_3"         to Pair(0.3024f, 0.3345f),
    "WALK_4"         to Pair(0.2137f, 0.3686f),
    "WALK_5"         to Pair(0.2137f, 0.4069f),
    "WALK_6"         to Pair(0.2137f, 0.4468f),
    "WALK_7"         to Pair(0.2137f, 0.4852f),
    "WALK_8"         to Pair(0.2137f, 0.5250f),
    "WALK_9"         to Pair(0.2137f, 0.5605f),
    "WALK_10"        to Pair(0.2137f, 0.5988f),
    "WALK_11"        to Pair(0.2137f, 0.6401f),
    "WALK_12"        to Pair(0.2517f, 0.6734f),
    "WALK_13"        to Pair(0.2531f, 0.7140f),
    "WALK_14"        to Pair(0.3038f, 0.7553f),
    "WALK_15"        to Pair(0.8612f, 0.7538f),
    "WALK_16"        to Pair(0.8612f, 0.7140f),
    "WALK_17"        to Pair(0.8612f, 0.6734f),
    "WALK_18"        to Pair(0.8612f, 0.6401f),
    "WALK_19"        to Pair(0.8612f, 0.5988f),
    "WALK_20"        to Pair(0.8612f, 0.5605f),
    "WALK_21"        to Pair(0.8612f, 0.5250f),
    "WALK_22"        to Pair(0.8612f, 0.4852f),
    "WALK_23"        to Pair(0.8612f, 0.4468f),
    "WALK_24"        to Pair(0.8612f, 0.4069f),
    "WALK_25"        to Pair(0.8612f, 0.3693f),
    "WALK_26"        to Pair(0.8612f, 0.3345f),
    "WALK_27"        to Pair(0.9091f, 0.7705f),
    "WALK_28"        to Pair(0.9443f, 0.4866f),
    "WALK_29"        to Pair(0.9471f, 0.6010f),
    "DELI"           to Pair(0.5698f, 0.3345f),
    "MEAT"           to Pair(1.0006f, 0.3794f),
    "BAKERY"         to Pair(0.9583f, 0.8031f),
    "FROZEN"         to Pair(0.4896f, 0.7140f),
    "PET"            to Pair(0.4896f, 0.5988f),
    "CLEANING"       to Pair(0.4896f, 0.5605f),
    "PERSONAL_CARE"  to Pair(0.4896f, 0.5206f),
    "BREAKFAST"      to Pair(0.4896f, 0.4852f),
    "PASTA_RICE"     to Pair(0.4896f, 0.4468f),
    "DISPENSA"       to Pair(0.4896f, 0.4069f),
    "DAIRY"          to Pair(0.4896f, 0.3686f),
)

// ── Nodes ──────────────────────────────────────────────────────────────────

data class Node(val id: String, val type: NodeType)

enum class NodeType { WALK, ZONE, ENTRANCE, CHECKOUT, EXIT }

// ── Arestes (sense weight: el calcula euclideanDist) ───────────────────────

data class Edge(val from: String, val to: String)

private fun euclideanDist(a: String, b: String): Float {
    val (ax, ay) = nodeCoordinates[a] ?: return Float.MAX_VALUE
    val (bx, by) = nodeCoordinates[b] ?: return Float.MAX_VALUE
    val dx = ax - bx
    val dy = ay - by
    return sqrt(dx * dx + dy * dy)
}

// ── Definició del graf ─────────────────────────────────────────────────────

val walkNodes = (1..30).map { i -> Node("WALK_$i", NodeType.WALK) }

val nodes = listOf(
    Node("FRESHPRODUCTS1", NodeType.ZONE),
    Node("FRESHPRODUCTS2", NodeType.ZONE),
    Node("FRESHPRODUCTS3", NodeType.ZONE),
    Node("FRESHPRODUCTS4", NodeType.ZONE),
    Node("FRESHPRODUCTS5", NodeType.ZONE),
    Node("FRESHPRODUCTS6", NodeType.ZONE),
    Node("BAKERY",         NodeType.ZONE),
    Node("PASTA_RICE",     NodeType.ZONE),
    Node("DISPENSA",       NodeType.ZONE),
    Node("DAIRY",          NodeType.ZONE),
    Node("DELI",           NodeType.ZONE),
    Node("MEAT",           NodeType.ZONE),
    Node("FROZEN",         NodeType.ZONE),
    Node("BREAKFAST",      NodeType.ZONE),
    Node("DRINKS",         NodeType.ZONE),
    Node("PERSONAL_CARE",  NodeType.ZONE),
    Node("CLEANING",       NodeType.ZONE),
    Node("PET",            NodeType.ZONE),
    Node("ENTRANCE",       NodeType.ENTRANCE),
    Node("EXIT",           NodeType.EXIT),
    Node("CHECKOUT1",      NodeType.CHECKOUT),
    Node("CHECKOUT2",      NodeType.CHECKOUT),
) + walkNodes

private val freshEdges = (1 until 6).flatMap { i ->
    listOf(
        Edge("FRESHPRODUCTS$i",     "FRESHPRODUCTS${i + 1}"),
        Edge("FRESHPRODUCTS${i + 1}", "FRESHPRODUCTS$i"),
    )
}

val edges = listOf(
    Edge("ENTRANCE",  "WALK_1"),
    Edge("WALK_1",    "FRESHPRODUCTS1"),
    Edge("FRESHPRODUCTS6", "WALK_2"),
    Edge("WALK_2",    "WALK_3"),
    Edge("WALK_3",    "CHECKOUT1"),
    Edge("WALK_3",    "WALK_4"),
    Edge("WALK_3",    "DELI"),
    Edge("WALK_4",    "CHECKOUT1"),
    Edge("WALK_4",    "WALK_5"),
    Edge("WALK_4",    "DAIRY"),
    Edge("WALK_5",    "DISPENSA"),
    Edge("WALK_5",    "CHECKOUT1"),
    Edge("WALK_5",    "WALK_6"),
    Edge("WALK_6",    "CHECKOUT1"),
    Edge("WALK_6",    "WALK_7"),
    Edge("WALK_6",    "PASTA_RICE"),
    Edge("WALK_7",    "BREAKFAST"),
    Edge("WALK_7",    "CHECKOUT2"),
    Edge("WALK_7",    "WALK_8"),
    Edge("WALK_8",    "CHECKOUT2"),
    Edge("WALK_8",    "WALK_9"),
    Edge("WALK_8",    "PERSONAL_CARE"),
    Edge("WALK_9",    "CHECKOUT2"),
    Edge("WALK_9",    "WALK_10"),
    Edge("WALK_9",    "CLEANING"),
    Edge("WALK_10",   "WALK_11"),
    Edge("WALK_10",   "CHECKOUT2"),
    Edge("WALK_10",   "PET"),
    Edge("WALK_11",   "CHECKOUT2"),
    Edge("WALK_11",   "WALK_12"),
    Edge("WALK_11",   "WALK_18"),
    Edge("WALK_12",   "WALK_13"),
    Edge("WALK_12",   "WALK_17"),
    Edge("WALK_13",   "WALK_14"),
    Edge("WALK_13",   "FROZEN"),
    Edge("WALK_14",   "WALK_15"),
    Edge("WALK_15",   "WALK_16"),
    Edge("WALK_15",   "WALK_27"),
    Edge("WALK_16",   "FROZEN"),
    Edge("WALK_16",   "WALK_17"),
    Edge("WALK_16",   "WALK_27"),
    Edge("WALK_17",   "WALK_27"),
    Edge("WALK_17",   "WALK_18"),
    Edge("WALK_18",   "WALK_29"),
    Edge("WALK_18",   "WALK_19"),
    Edge("WALK_19",   "PET"),
    Edge("WALK_19",   "WALK_20"),
    Edge("WALK_19",   "WALK_29"),
    Edge("WALK_20",   "CLEANING"),
    Edge("WALK_20",   "WALK_29"),
    Edge("WALK_20",   "WALK_21"),
    Edge("WALK_21",   "PERSONAL_CARE"),
    Edge("WALK_21",   "WALK_22"),
    Edge("WALK_21",   "WALK_28"),
    Edge("WALK_22",   "BREAKFAST"),
    Edge("WALK_22",   "WALK_23"),
    Edge("WALK_22",   "WALK_28"),
    Edge("WALK_23",   "PASTA_RICE"),
    Edge("WALK_23",   "WALK_24"),
    Edge("WALK_23",   "WALK_28"),
    Edge("WALK_24",   "DISPENSA"),
    Edge("WALK_24",   "WALK_25"),
    Edge("WALK_24",   "WALK_28"),
    Edge("WALK_25",   "WALK_28"),
    Edge("WALK_25",   "DAIRY"),
    Edge("WALK_25",   "WALK_26"),
    Edge("WALK_26",   "MEAT"),
    Edge("WALK_25",   "MEAT"),
    Edge("WALK_24",   "MEAT"),
    Edge("WALK_23",   "MEAT"),
    Edge("WALK_22",   "MEAT"),
    Edge("WALK_21",   "MEAT"),
    Edge("WALK_20",   "MEAT"),
    Edge("WALK_19",   "MEAT"),
    Edge("WALK_18",   "MEAT"),
    Edge("WALK_17",   "MEAT"),
    Edge("WALK_16",   "MEAT"),
    Edge("WALK_15",   "MEAT"),
    Edge("WALK_26",   "DELI"),
    Edge("WALK_26",   "WALK_28"),
    Edge("WALK_28",   "MEAT"),
    Edge("WALK_28",   "WALK_29"),
    Edge("WALK_29",   "WALK_27"),
    Edge("WALK_20",   "BAKERY"),
    Edge("WALK_19",   "BAKERY"),
    Edge("WALK_18",   "BAKERY"),
    Edge("WALK_17",   "BAKERY"),
    Edge("WALK_16",   "BAKERY"),
    Edge("WALK_15",   "BAKERY"),
    Edge("MEAT",      "BAKERY"),
) + freshEdges

// ── Tipus de resultat ──────────────────────────────────────────────────────

data class OptimizedRoute(
    val path: List<String>,
    val steps: List<RouteStep>,
    val pickSections: Set<String>,   // ← nou: node IDs on cal recollir coses
)

private val checkout2Sections = setOf(
    StoreSection.BREAKFAST,
    StoreSection.PERSONAL_CARE,
    StoreSection.CLEANING,
    StoreSection.PET,
    StoreSection.FROZEN
)

// ── Optimitzador ───────────────────────────────────────────────────────────

object RouteOptimizer {

    fun groupItemsByStoreSection(
        items: List<ShoppingItem>,
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

    // Graf bidireccional construït a partir de les arestes
    private val bidirectionalEdges = edges.flatMap { listOf(it, Edge(it.to, it.from)) }
    private val graph: Map<String, List<Edge>> = bidirectionalEdges.groupBy { it.from }

    private fun nodeIdToSection(nodeId: String): StoreSection? = when {
        nodeId.startsWith("FRESHPRODUCTS") -> StoreSection.FRESHPRODUCTS
        else -> StoreSection.entries.find { it.name == nodeId }
    }

    /**
     * Dijkstra global usant distàncies euclidianes com a cost.
     *
     * [itemSections]   → seccions que l'usuari vol visitar (per penalitzar desvios)
     * [nodePenalties]  → penalització extra per nodes ja travessats (evita backtracking)
     */
    private fun shortestPath(
        start: String,
        end: String,
        itemSections: Set<StoreSection>,
        nodePenalties: Map<String, Float> = emptyMap(),
    ): List<String> {
        val dist = mutableMapOf<String, Float>().withDefault { Float.MAX_VALUE }
        val prev = mutableMapOf<String, String?>()
        val unvisited = nodes.map { it.id }.toMutableSet()

        dist[start] = 0f

        while (unvisited.isNotEmpty()) {
            val current = unvisited.minByOrNull { dist.getValue(it) } ?: break
            if (current == end) break
            unvisited.remove(current)

            val currentDist = dist.getValue(current)
            if (currentDist == Float.MAX_VALUE) break

            for (edge in graph[current].orEmpty()) {
                val baseCost = euclideanDist(current, edge.to)

                // Penalitza zones que no cal visitar
                val destNode = nodes.find { it.id == edge.to }
                val zonePenalty = if (
                    destNode?.type == NodeType.ZONE &&
                    nodeIdToSection(edge.to) !in itemSections
                ) 0.05f else 0f

                // Penalitza nodes ja travessats anteriorment
                val visitPenalty = nodePenalties[edge.to] ?: 0f

                val newDist = currentDist + baseCost + zonePenalty + visitPenalty
                if (newDist < dist.getValue(edge.to)) {
                    dist[edge.to] = newDist
                    prev[edge.to] = current
                }
            }
        }

        // Reconstruir camí
        val path = mutableListOf<String>()
        var cur: String? = end
        while (cur != null) {
            path.add(cur)
            cur = prev[cur]
        }
        return path.reversed()
    }


    fun optimize(items: List<ShoppingItem>): OptimizedRoute {

        val grouped      = groupItemsByStoreSection(items)
        val itemSections = grouped.keys.toSet()

        val useCheckout2 = itemSections.any { it in checkout2Sections }
        val checkout     = if (useCheckout2) "CHECKOUT2" else "CHECKOUT1"

        // ── Mapeig secció ↔ índex de bit ─────────────────────────────────────
        val sectionList  = itemSections.toList()
        val sectionIndex = sectionList.withIndex().associate { (i, s) -> s to i }
        val fullMask     = (1 shl sectionList.size) - 1

        fun sectionBit(nodeId: String): Int {
            val section = nodeIdToSection(nodeId) ?: return -1
            return sectionIndex[section] ?: -1
        }

        // ── Estat: (nodeId, bitmask de seccions visitades) ────────────────────
        data class State(val nodeId: String, val mask: Int)

        val startState = State("ENTRANCE", 0)

        // ── Dijkstra multi-estat ──────────────────────────────────────────────
        val dist = mutableMapOf<State, Float>().withDefault { Float.MAX_VALUE }
        val prev = mutableMapOf<State, State?>()
        // Priority queue: (cost, state)
        val pq   = java.util.PriorityQueue<Pair<Float, State>>(compareBy { it.first })

        dist[startState] = 0f
        pq.add(0f to startState)

        while (pq.isNotEmpty()) {
            val (currentCost, current) = pq.poll()

            // Ja tenim un camí millor per aquest estat → descartar
            if (currentCost > dist.getValue(current)) continue

            // Parada: hem visitat totes les seccions i estem al checkout
            if (current.mask == fullMask && current.nodeId == checkout) break

            for (edge in graph[current.nodeId].orEmpty()) {

                val baseCost = euclideanDist(current.nodeId, edge.to)

                // Penalitza entrar a zones que no calen
                val destNode   = nodes.find { it.id == edge.to }
                val zonePenalty = if (
                    destNode?.type == NodeType.ZONE &&
                    nodeIdToSection(edge.to) !in itemSections
                ) 0.05f else 0f

                // Actualitza el bitmask si el node destí és una secció objectiu
                val bit     = sectionBit(edge.to)
                val newMask = if (bit >= 0) current.mask or (1 shl bit) else current.mask

                val newCost  = currentCost + baseCost + zonePenalty
                val newState = State(edge.to, newMask)

                if (newCost < dist.getValue(newState)) {
                    dist[newState]  = newCost
                    prev[newState]  = current
                    pq.add(newCost to newState)
                }
            }
        }

        // ── Reconstruir camí des de l'estat final ─────────────────────────────
        val endState = State(checkout, fullMask)
        val fullRoute = mutableListOf<String>()
        var cur: State? = endState
        while (cur != null) {
            fullRoute.add(cur.nodeId)
            cur = prev[cur]
        }
        fullRoute.reverse()

        Log.d("RouteOptimizer", "========== FINAL ROUTE (multi-state Dijkstra) ==========")
        fullRoute.forEach { Log.d("RouteOptimizer", it) }

        // ── Construir steps (igual que abans) ────────────────────────────────
        val steps             = mutableListOf<RouteStep>()
        val visitedSections   = mutableSetOf<StoreSection>()

        for (nodeId in fullRoute) {
            val section = nodeIdToSection(nodeId) ?: continue
            if (section in visitedSections) continue
            visitedSections.add(section)

            val sectionItems = grouped[section] ?: emptyList()
            if (sectionItems.isEmpty()) steps.add(RouteStep.PassThrough(section))
            else {
                steps.add(RouteStep.EnterSection(section))
                sectionItems.forEach { steps.add(RouteStep.PickItem(it)) }
            }
        }

        steps.add(RouteStep.GoToCheckout(checkout))
        steps.add(RouteStep.Finish("EXIT"))

        val unrecognized = items.filter { it.product == null && !it.checked }
        if (unrecognized.isNotEmpty()) steps.add(RouteStep.AskStaff(unrecognized))

        val pickSections = steps
            .filterIsInstance<RouteStep.EnterSection>()
            .map { if (it.section == StoreSection.FRESHPRODUCTS) "FRESHPRODUCTS1" else it.section.name }
            .toSet()

        return OptimizedRoute(path = fullRoute, steps = steps, pickSections = pickSections)
    }

    /**
     * Nearest-neighbour greedy per ordenar les seccions intermèdies.
     * Usa distàncies euclidianes directes (vol d'ocell) per estimar
     * l'ordre sense fer Dijkstra complet a cada pas.
     */
    private fun buildGreedyOrder(
        start: String,
        targets: List<String>,
        end: String,
    ): List<String> {
        val remaining = targets.toMutableList()
        val ordered = mutableListOf(start)
        var current = start

        while (remaining.isNotEmpty()) {
            val nearest = remaining.minByOrNull { euclideanDist(current, it) }!!
            ordered.add(nearest)
            remaining.remove(nearest)
            current = nearest
        }

        ordered.add(end)
        return ordered
    }
}

// ── Steps ──────────────────────────────────────────────────────────────────

sealed class RouteStep {
    data class EnterSection(val section: StoreSection)      : RouteStep()
    data class PassThrough(val section: StoreSection)       : RouteStep()
    data class PickItem(val item: ShoppingItem)             : RouteStep()
    data class GoToCheckout(val checkoutId: String)         : RouteStep()
    data class Finish(val exitId: String)                   : RouteStep()
    data class AskStaff(val items: List<ShoppingItem>)      : RouteStep()
}