package com.esselunga.navigator.ui.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esselunga.navigator.viewmodel.ShoppingViewModel

private val EsselungaGreen = Color(0xFF00843D)
private val AisleColor = Color(0xFFE3F2FD)
private val AisleBorder = Color(0xFF90CAF9)
private val RouteColor = Color(0xFFFF6F00)
private val WallColor = Color(0xFF424242)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: ShoppingViewModel,
    onBack: () -> Unit
) {
    val items by viewModel.items.collectAsState()
    val route = remember(items) { viewModel.route }

    // Collect which corsie are in the route
    val targetCorsie = remember(route) {
        route.filterIsInstance<com.esselunga.navigator.util.RouteStep.GoToAisle>()
            .map { it.corsia }
            .toSet()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mappa negozio", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EsselungaGreen,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Legend
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                LegendDot(color = AisleColor, label = "Corsie")
                LegendDot(color = Color(0xFFFFE082), label = "Da visitare")
                LegendDot(color = RouteColor, label = "Percorso")
            }

            // Store map canvas
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFFAFAFA))
                        .padding(12.dp)
                ) {
                    drawStoreLayout(targetCorsie)
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                "Mappa approssimativa — Esselunga Porta Vittoria",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

/**
 * Draws a simplified floor plan of Esselunga Porta Vittoria.
 * Layout: entrance bottom-left, produce on left, aisles 1-13 vertical,
 * dairy/deli/meat on back wall (right), frozen on far right.
 *
 * This is a schematic approximation — update coordinates once you have
 * the real store plan.
 */
private fun DrawScope.drawStoreLayout(targetCorsie: Set<Int>) {
    val w = size.width
    val h = size.height

    // Margins
    val marginLeft = w * 0.08f
    val marginTop = h * 0.05f
    val storeW = w * 0.84f
    val storeH = h * 0.88f

    // Store outer wall
    drawRect(
        color = WallColor,
        topLeft = Offset(marginLeft, marginTop),
        size = Size(storeW, storeH),
        style = Stroke(width = 4f)
    )

    // Floor fill
    drawRect(
        color = Color(0xFFF5F5F5),
        topLeft = Offset(marginLeft + 2f, marginTop + 2f),
        size = Size(storeW - 4f, storeH - 4f)
    )

    // Entrance (bottom left gap in wall)
    drawRect(
        color = Color(0xFFF5F5F5),
        topLeft = Offset(marginLeft, h * 0.91f),
        size = Size(w * 0.12f, h * 0.04f)
    )
    drawLine(Color.White, Offset(marginLeft, h * 0.91f), Offset(marginLeft, h * 0.95f), 4f)
    drawLine(Color.White, Offset(marginLeft + w * 0.12f, h * 0.91f), Offset(marginLeft + w * 0.12f, h * 0.95f), 4f)

    // "ENTRATA" label
    drawContext.canvas.nativeCanvas.drawText(
        "ENTRATA",
        marginLeft + 4f,
        h * 0.98f,
        android.graphics.Paint().apply {
            this.color = android.graphics.Color.DKGRAY
            textSize = 24f
        }
    )

    // Produce zone (left column, top)
    drawSection(
        label = "Frutta/Verdura",
        color = Color(0xFFC8E6C9),
        left = marginLeft + 4f,
        top = marginTop + 4f,
        width = storeW * 0.18f,
        height = storeH * 0.28f,
        highlight = 1 in targetCorsie || 0 in targetCorsie
    )

    // Bakery (left column, below produce)
    drawSection(
        label = "Pane",
        color = Color(0xFFFFF9C4),
        left = marginLeft + 4f,
        top = marginTop + storeH * 0.30f,
        width = storeW * 0.18f,
        height = storeH * 0.15f,
        highlight = 2 in targetCorsie
    )

    // Main aisle block — 10 vertical aisles
    val aisleAreaLeft = marginLeft + storeW * 0.20f
    val aisleAreaTop = marginTop + 4f
    val aisleAreaW = storeW * 0.55f
    val aisleAreaH = storeH * 0.85f
    val numAisles = 10
    val aisleW = aisleAreaW / numAisles

    for (i in 0 until numAisles) {
        val corsiaNum = i + 3   // corsie 3-12
        val isHighlighted = corsiaNum in targetCorsie
        val aisleLeft = aisleAreaLeft + i * aisleW

        // Shelf block
        drawRect(
            color = if (isHighlighted) Color(0xFFFFE082) else AisleColor,
            topLeft = Offset(aisleLeft + 2f, aisleAreaTop + 2f),
            size = Size(aisleW - 4f, aisleAreaH - 4f)
        )
        drawRect(
            color = if (isHighlighted) Color(0xFFFFC107) else AisleBorder,
            topLeft = Offset(aisleLeft + 2f, aisleAreaTop + 2f),
            size = Size(aisleW - 4f, aisleAreaH - 4f),
            style = Stroke(width = 1.5f)
        )

        // Corsia number label
        drawContext.canvas.nativeCanvas.drawText(
            "${corsiaNum}",
            aisleLeft + aisleW / 2 - 12f,
            aisleAreaTop + aisleAreaH / 2,
            android.graphics.Paint().apply {
                this.color = if (isHighlighted) android.graphics.Color.parseColor("#E65100")
                else android.graphics.Color.parseColor("#1565C0")
                textSize = 28f
                isFakeBoldText = isHighlighted
            }
        )
    }

    // Back wall: dairy + deli + meat (right side)
    val backLeft = marginLeft + storeW * 0.77f
    val backW = storeW * 0.21f

    drawSection("Latte/Latticini", Color(0xFFE1F5FE), backLeft + 2f, marginTop + 4f, backW - 4f, storeH * 0.22f, 14 in targetCorsie)
    drawSection("Salumi/Formaggi", Color(0xFFFCE4EC), backLeft + 2f, marginTop + storeH * 0.24f, backW - 4f, storeH * 0.18f, 15 in targetCorsie)
    drawSection("Carne/Pesce", Color(0xFFFFCDD2), backLeft + 2f, marginTop + storeH * 0.44f, backW - 4f, storeH * 0.20f, 16 in targetCorsie || 17 in targetCorsie)
    drawSection("Surgelati", Color(0xFFE3F2FD), backLeft + 2f, marginTop + storeH * 0.66f, backW - 4f, storeH * 0.22f, 18 in targetCorsie)

    // Draw route path (simplified: vertical line through highlighted aisles)
    if (targetCorsie.isNotEmpty()) {
        val sorted = targetCorsie.sorted()
        val pathPoints = sorted.mapNotNull { corsia ->
            val aisleIdx = corsia - 3
            if (aisleIdx in 0 until numAisles) {
                Offset(aisleAreaLeft + aisleIdx * aisleW + aisleW / 2, aisleAreaTop + aisleAreaH / 2)
            } else null
        }

        if (pathPoints.size >= 2) {
            val path = Path().apply {
                moveTo(marginLeft + storeW * 0.10f, h * 0.91f)  // entrance
                lineTo(pathPoints.first().x, h * 0.91f)
                pathPoints.forEach { lineTo(it.x, it.y) }
            }
            drawPath(path, RouteColor, style = Stroke(width = 5f))
        }
    }
}

private fun DrawScope.drawSection(
    label: String,
    color: Color,
    left: Float,
    top: Float,
    width: Float,
    height: Float,
    highlight: Boolean
) {
    drawRect(
        color = if (highlight) Color(0xFFFFE082) else color,
        topLeft = Offset(left, top),
        size = Size(width, height)
    )
    drawRect(
        color = if (highlight) Color(0xFFFFC107) else color.copy(alpha = 0.6f),
        topLeft = Offset(left, top),
        size = Size(width, height),
        style = Stroke(width = 1.5f)
    )
    val lines = label.split("/")
    lines.forEachIndexed { idx, line ->
        drawContext.canvas.nativeCanvas.drawText(
            line,
            left + 4f,
            top + 28f + idx * 26f,
            android.graphics.Paint().apply {
                this.color = android.graphics.Color.DKGRAY
                textSize = 22f
            }
        )
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(14.dp)
                .background(color, RoundedCornerShape(3.dp))
        )
        Spacer(Modifier.width(4.dp))
        Text(label, fontSize = 12.sp, color = Color.DarkGray)
    }
}
