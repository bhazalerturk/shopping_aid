package com.esselunga.navigator.util

import com.esselunga.navigator.data.StoreSection
import kotlin.math.atan2
import kotlin.math.abs

// ── Aisle numbers ─────────────────────────────────────────────────────────────
// Sections without a number (Fresh products, Bakery, Deli, Meat) are null —
// they are landmark areas the user can see directly, no aisle sign needed.


val sectionAisle: Map<StoreSection, Int?> = mapOf(
    StoreSection.FRESHPRODUCTS to null,
    StoreSection.BAKERY        to null,
    StoreSection.DELI          to null,
    StoreSection.MEAT          to null,
    StoreSection.DAIRY         to 1,
    StoreSection.DISPENSA      to 2,
    StoreSection.PASTA_RICE    to 3,
    StoreSection.BREAKFAST     to 4,
    StoreSection.PERSONAL_CARE to 5,
    StoreSection.CLEANING      to 6,
    StoreSection.PET           to 7,
    StoreSection.FROZEN        to 10,
    StoreSection.DRINKS        to 11,
)
// ── Direction ─────────────────────────────────────────────────────────────────

enum class WalkDirection {
    STRAIGHT, LEFT, RIGHT, BEHIND;

    val emoji: String get() = when (this) {
        STRAIGHT -> "⬆️"
        LEFT     -> "⬅️"
        RIGHT    -> "➡️"
        BEHIND   -> "🔄"
    }

    val label: String get() = when (this) {
        STRAIGHT -> "Go straight"
        LEFT     -> "Turn left"
        RIGHT    -> "Turn right"
        BEHIND   -> "Turn around"
    }
}

// Node coordinates (normalised 0..1) — mirrors the map, used for direction math
private val nodeCoords: Map<String, Pair<Float, Float>> = mapOf(
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

// ── Direction calculation ─────────────────────────────────────────────────────
//
// Given the ordered list of node IDs in the route path, and the index of the
// node we are about to visit, we calculate what direction the user needs to
// walk from the PREVIOUS node to the CURRENT node — relative to the heading
// they were already walking (from the node before the previous one).
//
// If there is no previous heading (first step), we use the absolute angle
// from ENTRANCE toward the target and describe it as a global direction.
//
// Angle convention (screen coords — Y increases downward):
//   0°   = right  (east)
//   90°  = down   (south)
//   180° = left   (west)
//   -90° = up     (north)
//
// We classify the RELATIVE turn angle into four buckets:
//   -45°..45°   → STRAIGHT
//   45°..135°   → RIGHT  (clockwise turn)
//   -135°..-45° → LEFT   (counter-clockwise)
//   else        → BEHIND

fun computeDirection(
    routePath: List<String>,
    targetNodeIndex: Int              // index of the destination node in routePath
): WalkDirection {
    if (targetNodeIndex <= 0) return WalkDirection.STRAIGHT

    val targetId   = routePath[targetNodeIndex]
    val previousId = routePath[targetNodeIndex - 1]

    val (tx, ty) = nodeCoords[targetId]   ?: return WalkDirection.STRAIGHT
    val (px, py) = nodeCoords[previousId] ?: return WalkDirection.STRAIGHT

    // Angle of the current segment (previous → target)
    val currentAngle = atan2(ty - py, tx - px)   // radians

    if (targetNodeIndex < 2) {
        // First segment — no prior heading, use absolute angle
        return absoluteDirection(currentAngle)
    }

    val beforeId = routePath[targetNodeIndex - 2]
    val (bx, by) = nodeCoords[beforeId] ?: return absoluteDirection(currentAngle)

    // Angle of the prior segment (before → previous)
    val priorAngle = atan2(py - by, px - bx)

    // Relative turn: positive = clockwise (right), negative = counter-clockwise (left)
    var delta = Math.toDegrees((currentAngle - priorAngle).toDouble())
    // Normalise to -180..180
    while (delta >  180) delta -= 360
    while (delta < -180) delta += 360

    return when {
        abs(delta) <= 45   -> WalkDirection.STRAIGHT
        delta in 45.0..135.0  -> WalkDirection.RIGHT
        delta in -135.0..-45.0 -> WalkDirection.LEFT
        else               -> WalkDirection.BEHIND
    }
}

// Converts an absolute screen angle to a simple cardinal direction.
// Used only for the very first step where there is no prior heading.
private fun absoluteDirection(angleRad: Float): WalkDirection {
    val deg = Math.toDegrees(angleRad.toDouble())
    return when {
        deg in -45.0..45.0    -> WalkDirection.RIGHT    // heading east
        deg in 45.0..135.0    -> WalkDirection.STRAIGHT // heading south (into store)
        abs(deg) >= 135       -> WalkDirection.LEFT     // heading west
        else                  -> WalkDirection.STRAIGHT
    }
}

// ── Convenience: find the route path index for a given section node ───────────
// Returns the index in routePath where this section first appears.
fun indexOfSectionInPath(routePath: List<String>, section: StoreSection): Int {
    val candidates = when (section) {
        StoreSection.FRESHPRODUCTS ->
            listOf("FRESHPRODUCTS1","FRESHPRODUCTS2","FRESHPRODUCTS3",
                   "FRESHPRODUCTS4","FRESHPRODUCTS5","FRESHPRODUCTS6")
        else -> listOf(section.name)
    }
    return routePath.indexOfFirst { it in candidates }
}
