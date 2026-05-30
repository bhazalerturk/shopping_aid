package com.esselunga.navigator.ui.map

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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esselunga.navigator.ui.theme.EasylungaGreen
import com.esselunga.navigator.util.RouteStep
import com.esselunga.navigator.viewmodel.ShoppingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: ShoppingViewModel,
    onBack: () -> Unit,
    onHelp: () -> Unit
) {
    val route = remember(viewModel.items.collectAsState().value) { viewModel.route }
    val activeAisles = route.filterIsInstance<RouteStep.GoToAisle>().map { it.corsia }.toSet()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Store Map", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EasylungaGreen, titleContentColor = Color.White),
                actions = {
                    IconButton(onClick = onHelp) {
                        Icon(Icons.Default.Info, contentDescription = "Help", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LegendItem(color = Color(0xFFFFE082), label = "Your route")
                LegendItem(color = Color(0xFFE0E0E0), label = "Other aisles")
                LegendItem(color = Color(0xFF81C784), label = "Sections")
            }

            // Map canvas
            Card(
                modifier = Modifier.fillMaxWidth().weight(1f),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                    drawStoreMap(activeAisles)
                }
            }

            // Active aisle list
            if (activeAisles.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(Modifier.fillMaxWidth().padding(12.dp)) {
                        Text("Your route visits:", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = EasylungaGreen)
                        Text(
                            activeAisles.sorted().joinToString(", ") { "Aisle $it" },
                            fontSize = 14.sp, color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(Modifier.size(16.dp).background(color, RoundedCornerShape(3.dp)))
        Text(label, fontSize = 13.sp, color = Color.DarkGray)
    }
}

private fun DrawScope.drawStoreMap(activeAisles: Set<Int>) {
    val w = size.width
    val h = size.height
    val paint = android.graphics.Paint().apply {
        isAntiAlias = true
        textAlign = android.graphics.Paint.Align.CENTER
    }

    // Background
    drawRect(color = Color(0xFFF5F5F5))

    // Store outline
    drawRect(color = Color.LightGray, topLeft = Offset(10f, 10f), size = Size(w - 20f, h - 20f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))

    // Aisles (columns across middle of store)
    val aisleCount = 12
    val aisleStart = h * 0.20f
    val aisleEnd = h * 0.78f
    val aisleHeight = aisleEnd - aisleStart
    val leftMargin = w * 0.08f
    val rightMargin = w * 0.85f
    val aisleWidth = (rightMargin - leftMargin) / aisleCount

    for (i in 1..aisleCount) {
        val x = leftMargin + (i - 1) * aisleWidth
        val isActive = i in activeAisles
        val aisleColor = if (isActive) Color(0xFFFFE082) else Color(0xFFE0E0E0)

        drawRect(
            color = aisleColor,
            topLeft = Offset(x + 2f, aisleStart),
            size = Size(aisleWidth - 4f, aisleHeight)
        )

        // Aisle number label
        drawContext.canvas.nativeCanvas.apply {
            paint.textSize = 28f
            paint.color = if (isActive) android.graphics.Color.rgb(0, 80, 30) else android.graphics.Color.GRAY
            paint.isFakeBoldText = isActive
            drawText("$i", x + aisleWidth / 2, aisleStart - 14f, paint)
        }
    }

    // Section areas
    val sectionY = h * 0.80f
    val sectionH = h * 0.13f

    // Back wall sections
    val sections = listOf(
        "Dairy" to 0.08f,
        "Deli" to 0.30f,
        "Meat" to 0.52f,
        "Frozen" to 0.74f
    )
    for ((name, xRatio) in sections) {
        val sx = w * xRatio + leftMargin * 0.5f
        val sectionW = w * 0.18f
        drawRect(color = Color(0xFF81C784), topLeft = Offset(sx, sectionY), size = Size(sectionW, sectionH))
        drawContext.canvas.nativeCanvas.apply {
            paint.textSize = 24f
            paint.color = android.graphics.Color.rgb(0, 60, 20)
            paint.isFakeBoldText = false
            drawText(name, sx + sectionW / 2, sectionY + sectionH / 2 + 10f, paint)
        }
    }

    // Produce section (left side, entrance area)
    drawRect(color = Color(0xFFA5D6A7), topLeft = Offset(10f, h * 0.06f), size = Size(w * 0.06f, h * 0.12f))
    drawContext.canvas.nativeCanvas.apply {
        paint.textSize = 22f
        paint.color = android.graphics.Color.rgb(0, 60, 20)
        drawText("Produce", w * 0.03f, h * 0.14f, paint)
    }

    // Entrance arrow
    drawContext.canvas.nativeCanvas.apply {
        paint.textSize = 30f
        paint.color = android.graphics.Color.rgb(0, 100, 40)
        drawText("▼ Entrance", w * 0.5f, h * 0.97f, paint)
    }
}
