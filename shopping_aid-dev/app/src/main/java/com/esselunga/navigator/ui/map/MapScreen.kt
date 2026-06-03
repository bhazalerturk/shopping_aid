package com.esselunga.navigator.ui.map

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esselunga.navigator.R
import com.esselunga.navigator.util.OptimizedRoute
import com.esselunga.navigator.util.RouteStep

private val EsselungaGreen = Color(0xFF00843D)
private val RouteColor      = Color(0xFFFFB300)   // amber route line
private val StopColor       = Color(0xFF00843D)    // green stop dots
private val CurrentColor    = Color(0xFFE53935)    // red pulsing dot (current step)
private val PassColor       = Color(0xFF90A4AE)    // grey for pass-through

// ─────────────────────────────────────────────────────────────────────────────
// NODE COORDINATES
// Provide (x, y) as fractions of the map image width/height (0f..1f).
// Measure pixel positions on your PNG, then divide by (imageWidth, imageHeight).
// ─────────────────────────────────────────────────────────────────────────────
private val nodeCoordinates: Map<String, Pair<Float, Float>> = mapOf(

    // ── Entrance / Exit ─────────────────────────────
    "ENTRANCE" to Pair(0.0701f, 0.2107f),
    "EXIT"     to Pair(0.0335f, 0.7502f),

    // ── Checkout ────────────────────────────────────
    "CHECKOUT1" to Pair(0.1151f, 0.4193f),
    "CHECKOUT2" to Pair(0.1137f, 0.6119f),

    // ── Fresh products ──────────────────────────────
    "FRESHPRODUCTS1" to Pair(0.2038f, 0.2476f),
    "FRESHPRODUCTS2" to Pair(0.2038f, 0.2013f),
    "FRESHPRODUCTS3" to Pair(0.4305f, 0.2013f),
    "FRESHPRODUCTS4" to Pair(0.6374f, 0.2013f),
    "FRESHPRODUCTS5" to Pair(0.6374f, 0.2592f),
    "FRESHPRODUCTS6" to Pair(0.4276f, 0.2592f),

    // ── WALK corridor ───────────────────────────────
    "WALK_1"  to Pair(0.1250f, 0.2469f),
    "WALK_2"  to Pair(0.3024f, 0.2592f),
    "WALK_3"  to Pair(0.3024f, 0.3345f),
    "WALK_4"  to Pair(0.2137f, 0.3686f),
    "WALK_5"  to Pair(0.2137f, 0.4069f),
    "WALK_6"  to Pair(0.2137f, 0.4468f),
    "WALK_7"  to Pair(0.2137f, 0.4852f),
    "WALK_8"  to Pair(0.2137f, 0.5250f),
    "WALK_9"  to Pair(0.2137f, 0.5605f),
    "WALK_10" to Pair(0.2137f, 0.5988f),
    "WALK_11" to Pair(0.2137f, 0.6401f),
    "WALK_12" to Pair(0.2517f, 0.6734f),
    "WALK_13" to Pair(0.2531f, 0.7140f),
    "WALK_14" to Pair(0.3038f, 0.7553f),

    "WALK_15" to Pair(0.8612f, 0.7538f),
    "WALK_16" to Pair(0.8612f, 0.7140f),
    "WALK_17" to Pair(0.8612f, 0.6734f),
    "WALK_18" to Pair(0.8612f, 0.6401f),
    "WALK_19" to Pair(0.8612f, 0.5988f),
    "WALK_20" to Pair(0.8612f, 0.5605f),
    "WALK_21" to Pair(0.8612f, 0.5250f),
    "WALK_22" to Pair(0.8612f, 0.4852f),
    "WALK_23" to Pair(0.8612f, 0.4468f),
    "WALK_24" to Pair(0.8612f, 0.4069f),
    "WALK_25" to Pair(0.8612f, 0.3693f),
    "WALK_26" to Pair(0.8612f, 0.3345f),

    "WALK_27" to Pair(0.9091f, 0.7705f),
    "WALK_28" to Pair(0.9443f, 0.4866f),
    "WALK_29" to Pair(0.9471f, 0.6010f),
    "WALK_30" to Pair(0.2137f, 0.3345f),


    // ── Sections ────────────────────────────────────
    "DELI"          to Pair(0.5698f, 0.3345f),
    "MEAT"          to Pair(1.0006f, 0.3794f),
    "BAKERY"        to Pair(0.9583f, 0.8031f),

    "FROZEN"        to Pair(0.4896f, 0.7140f),
    "PET"           to Pair(0.4896f, 0.5988f),
    "CLEANING"      to Pair(0.4896f, 0.5605f),
    "PERSONAL_CARE" to Pair(0.4896f, 0.5206f),
    "BREAKFAST"     to Pair(0.4896f, 0.4852f),
    "PASTA_RICE"    to Pair(0.4896f, 0.4468f),
    "DISPENSA"      to Pair(0.4896f, 0.4069f),
    "DAIRY"         to Pair(0.4896f, 0.3686f),

    // ── Boundaries ──────────────────────────────────
    "a" to Pair(0.0f, 0.16f),
    "b" to Pair(1.0f, 0.16f),
    "c" to Pair(0.0f, 0.84f),
    "d" to Pair(1.0f, 0.84f)
)

// ─────────────────────────────────────────────────────────────────────────────
// Helper: zone nodes that deserve a visible label dot on the map
// ─────────────────────────────────────────────────────────────────────────────
private val sectionNodes = setOf(
    "ENTRANCE", "EXIT",
    "CHECKOUT1", "CHECKOUT2",
    "FRESHPRODUCTS1", "FRESHPRODUCTS2", "FRESHPRODUCTS3",
    "FRESHPRODUCTS4", "FRESHPRODUCTS5", "FRESHPRODUCTS6",
    "BAKERY", "PASTA_RICE", "DISPENSA", "DAIRY", "DELI",
    "MEAT", "FROZEN", "BREAKFAST", "DRINKS",
    "PERSONAL_CARE", "CLEANING", "PET"
)

// ─────────────────────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    route: OptimizedRoute,               // pass viewModel.route from the caller
    currentStepIndex: Int = 0,           // pass the current navigation step index
    onBack: () -> Unit,
    onHelp: () -> Unit
) {
    // Animated dash offset → creates the "marching ants" / drawing effect
    val infiniteTransition = rememberInfiniteTransition(label = "dash")
    val dashOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue  = 40f,              // must match pathEffect interval sum
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing)
        ),
        label = "dashOffset"
    )

    // Pulsing scale for the current-position dot
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue  = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Build the ordered list of node IDs from the route path
    val routePath = route.path   // List<String> of node IDs in visit order

    // Figure out which node corresponds to the current step so we can show
    // the pulsing dot. We look at the non-PickItem step at currentStepIndex.
    val currentNodeId: String? = remember(route.steps, currentStepIndex) {
        val step = route.steps.getOrNull(currentStepIndex) ?: return@remember null
        when {
            currentStepIndex == 0 -> "ENTRANCE"
            step is RouteStep.EnterSection -> step.section.name
                .let { if (it == "FRESHPRODUCTS") "FRESHPRODUCTS1" else it }
            step is RouteStep.PassThrough  -> step.section.name
            step is RouteStep.PickItem -> {
                // Busca l'EnterSection més proper anterior
                val parentSection = route.steps
                    .subList(0, currentStepIndex)
                    .filterIsInstance<RouteStep.EnterSection>()
                    .lastOrNull()?.section?.name
                parentSection?.let { if (it == "FRESHPRODUCTS") "FRESHPRODUCTS1" else it }
            }
            step is RouteStep.GoToCheckout -> step.checkoutId
            step is RouteStep.Finish       -> "EXIT"
            else                      -> null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Store Map",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onHelp) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Help",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = EsselungaGreen,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // ── Legend ───────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                LegendItem(color = RouteColor,   label = "Your route")
                LegendItem(color = CurrentColor, label = "You are here")
                LegendItem(color = StopColor,    label = "Stop")
            }

            // ── Map + overlay ─────────────────────────────────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth().weight(1f),
                shape     = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Box(Modifier.fillMaxSize()) {

                    // Background PNG map
                    // ⚠️  Replace R.drawable.store_map with your actual drawable name
                    androidx.compose.foundation.Image(
                        painter            = painterResource(id = R.drawable.store_map),
                        contentDescription = "Store map",
                        modifier           = Modifier.fillMaxSize(),
                        contentScale       = ContentScale.Fit
                    )

                    // Route overlay drawn on top
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRouteOverlay(
                            routePath     = routePath,
                            pickSections  = route.pickSections,
                            currentNodeId = currentNodeId,
                            dashOffset    = dashOffset,
                            pulseScale    = pulseScale
                        )
                    }
                }
            }
        }
    }
}



// ─────────────────────────────────────────────────────────────────────────────
// Drawing logic
// ─────────────────────────────────────────────────────────────────────────────
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawRouteOverlay(
    routePath: List<String>,
    pickSections : Set<String>,
    currentNodeId: String?,
    dashOffset: Float,
    pulseScale: Float
) {
    val w = size.width
    val h = size.height

    // Convert normalized coords → pixel offsets for this canvas size
    fun nodeOffset(id: String): Offset? {
        val (nx, ny) = nodeCoordinates[id] ?: return null
        return Offset(nx * w, ny * h)
    }

    // ── 1. Draw path segments ─────────────────────────────────────────────
    val pathEffect = PathEffect.dashPathEffect(
        intervals  = floatArrayOf(20f, 20f),
        phase      = dashOffset
    )

    for (i in 0 until routePath.size - 1) {
        val from = nodeOffset(routePath[i])   ?: continue
        val to   = nodeOffset(routePath[i+1]) ?: continue

        // Solid underline (shadow / track)
        drawLine(
            color       = RouteColor.copy(alpha = 0.25f),
            start       = from,
            end         = to,
            strokeWidth = 10f,
            cap         = StrokeCap.Round
        )
        // Animated dashed line on top
        drawLine(
            color       = RouteColor,
            start       = from,
            end         = to,
            strokeWidth = 6f,
            cap         = StrokeCap.Round,
            pathEffect  = pathEffect
        )
    }

    // ── 2. Draw stop dots at section nodes ────────────────────────────────
    for (nodeId in routePath) {
        if (nodeId !in pickSections) continue   // ← filtre clau
        val offset = nodeOffset(nodeId) ?: continue
        if (nodeId == currentNodeId) continue

        drawCircle(color = Color.White, radius = 12f, center = offset)
        drawCircle(color = StopColor,   radius = 10f, center = offset)
    }

    // ── 3. Draw pulsing current-position dot ─────────────────────────────
    if (currentNodeId != null) {
        val currentOffset = nodeOffset(currentNodeId)
        if (currentOffset != null) {
            // Outer pulse ring
            drawCircle(
                color  = CurrentColor.copy(alpha = 0.30f),
                radius = 22f * pulseScale,
                center = currentOffset
            )
            // Solid dot
            drawCircle(color = Color.White,    radius = 14f, center = currentOffset)
            drawCircle(color = CurrentColor,   radius = 11f, center = currentOffset)
        }
    }

    // ── 4. Start / End markers ────────────────────────────────────────────
    routePath.firstOrNull()?.let { startId ->
        if (startId != currentNodeId) {
            nodeOffset(startId)?.let { o ->
                drawCircle(color = EsselungaGreen.copy(alpha = 0.5f), radius = 18f, center = o)
                drawCircle(color = EsselungaGreen, radius = 11f, center = o)
            }
        }
    }
    routePath.lastOrNull()?.let { endId ->
        nodeOffset(endId)?.let { o ->
            drawCircle(
                color  = EsselungaGreen,
                radius = 14f,
                center = o,
                style  = Stroke(width = 4f)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Legend chip
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment    = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            Modifier
                .size(14.dp)
                .background(color, RoundedCornerShape(3.dp))
        )
        Text(label, fontSize = 13.sp, color = Color.DarkGray)
    }
}