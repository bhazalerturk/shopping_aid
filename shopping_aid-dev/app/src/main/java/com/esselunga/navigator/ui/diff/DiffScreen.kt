package com.esselunga.navigator.ui.diff

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esselunga.navigator.data.ItemChange
import com.esselunga.navigator.data.ItemChangeType
import com.esselunga.navigator.data.ListDiff
import com.esselunga.navigator.util.BudgetCalculator

private val EasylungaGreen = Color(0xFF00843D)
private val AddedGreen = Color(0xFF4CAF50)
private val RemovedRed = Color(0xFFF44336)
private val ChangedYellow = Color(0xFFFFC107)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiffScreen(
    diff: ListDiff,
    onBack: () -> Unit,
    onAccept: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Changes from Caregiver",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EasylungaGreen,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Button(
                onClick = onAccept,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EasylungaGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Accept Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (diff.changes.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No changes made by the caregiver", fontSize = 16.sp, color = Color.Gray)
                        }
                    }
                }
            } else {
                // Added items (green)
                val addedItems = diff.changes.filter { it.changeType == ItemChangeType.ADDED }
                if (addedItems.isNotEmpty()) {
                    item {
                        Text(
                            "✅ Added (${addedItems.size})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AddedGreen
                        )
                    }
                    items(addedItems) { change ->
                        ItemChangeCard(change, ItemChangeType.ADDED)
                    }
                }

                // Items removed (rojo)
                val removedItems = diff.changes.filter { it.changeType == ItemChangeType.REMOVED }
                if (removedItems.isNotEmpty()) {
                    item {
                        Text(
                            "❌ Removed (${removedItems.size})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = RemovedRed
                        )
                    }
                    items(removedItems) { change ->
                        ItemChangeCard(change, ItemChangeType.REMOVED)
                    }
                }

                // Items con cantidad cambiada (amarillo)
                val quantityChanged = diff.changes.filter { it.changeType == ItemChangeType.QUANTITY_CHANGED }
                if (quantityChanged.isNotEmpty()) {
                    item {
                        Text(
                            "⚠️ Quantity Changed (${quantityChanged.size})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChangedYellow
                        )
                    }
                    items(quantityChanged) { change ->
                        ItemChangeCard(change, ItemChangeType.QUANTITY_CHANGED)
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun ItemChangeCard(change: ItemChange, changeType: ItemChangeType) {
    val backgroundColor = when (changeType) {
        ItemChangeType.ADDED -> Color(0xFFE8F5E9)
        ItemChangeType.REMOVED -> Color(0xFFFFEBEE)
        ItemChangeType.QUANTITY_CHANGED -> Color(0xFFFFF9C4)
        else -> Color.White
    }

    val borderColor = when (changeType) {
        ItemChangeType.ADDED -> AddedGreen
        ItemChangeType.REMOVED -> RemovedRed
        ItemChangeType.QUANTITY_CHANGED -> ChangedYellow
        else -> Color.LightGray
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, borderColor, RoundedCornerShape(10.dp))
    ) {
        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    change.item.rawText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    modifier = if (changeType == ItemChangeType.REMOVED) {
                        Modifier.strikethrough()
                    } else {
                        Modifier
                    },
                    textDecoration = if (changeType == ItemChangeType.REMOVED) {
                        TextDecoration.LineThrough
                    } else {
                        TextDecoration.None
                    }
                )

                when (changeType) {
                    ItemChangeType.ADDED -> {
                        Text("New", fontSize = 12.sp, color = AddedGreen, fontWeight = FontWeight.Bold)
                    }
                    ItemChangeType.REMOVED -> {
                        Text("Removed", fontSize = 12.sp, color = RemovedRed, fontWeight = FontWeight.Bold)
                    }
                    ItemChangeType.QUANTITY_CHANGED -> {
                        Text(
                            "x${change.originalQuantity} → x${change.item.quantity}",
                            fontSize = 12.sp,
                            color = ChangedYellow,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    else -> {}
                }
            }

            // Mostrar precio si está disponible
            if (change.item.priceEuro > 0) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Qty: x${change.item.quantity}", fontSize = 13.sp, color = Color.Gray)
                    Text(
                        BudgetCalculator.formatEuro(change.item.totalPrice),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

fun Modifier.strikethrough(): Modifier {
    return this.then(
        Modifier.padding(0.dp)
    )
}
