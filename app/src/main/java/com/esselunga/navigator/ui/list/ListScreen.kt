package com.esselunga.navigator.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esselunga.navigator.data.ShoppingItem
import com.esselunga.navigator.viewmodel.ShoppingViewModel

private val EsselungaGreen = Color(0xFF00843D)
private val UnknownOrange = Color(0xFFF57C00)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    viewModel: ShoppingViewModel,
    onStartNavigation: () -> Unit
) {
    val items by viewModel.items.collectAsState()
    var inputText by remember { mutableStateOf("") }

    fun submit() {
        viewModel.addItem(inputText)
        inputText = ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista della spesa", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EsselungaGreen,
                    titleContentColor = Color.White
                ),
                actions = {
                    if (items.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clearAll() }) {
                            Text("Svuota", color = Color.White)
                        }
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Input row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Aggiungi prodotto…") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { submit() }),
                        shape = RoundedCornerShape(12.dp)
                    )
                    IconButton(
                        onClick = { submit() },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = EsselungaGreen)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Aggiungi", tint = Color.White)
                    }
                }

                // Start navigation button
                Button(
                    onClick = onStartNavigation,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = items.any { !it.checked },
                    colors = ButtonDefaults.buttonColors(containerColor = EsselungaGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Avvia navigazione", fontSize = 16.sp)
                }
            }
        }
    ) { paddingValues ->
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aggiungi prodotti alla lista\nper iniziare la navigazione",
                    color = Color.Gray,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    ShoppingItemRow(
                        item = item,
                        onToggle = { viewModel.toggleChecked(item.id) },
                        onRemove = { viewModel.removeItem(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ShoppingItemRow(
    item: ShoppingItem,
    onToggle: () -> Unit,
    onRemove: () -> Unit
) {
    val bgColor = when {
        item.checked -> Color(0xFFF5F5F5)
        item.category == null -> Color(0xFFFFF3E0)
        else -> Color.White
    }
    val accentColor = when {
        item.category == null -> UnknownOrange
        else -> EsselungaGreen
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.checked,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = EsselungaGreen)
            )
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.rawText,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    textDecoration = if (item.checked) TextDecoration.LineThrough else null,
                    color = if (item.checked) Color.Gray else Color.Black
                )
                if (item.category != null) {
                    Text(
                        text = "Corsia ${item.category.corsia} · ${item.category.section.label}",
                        fontSize = 12.sp,
                        color = accentColor
                    )
                } else {
                    Text(
                        text = "Prodotto non riconosciuto — chiedi al personale",
                        fontSize = 12.sp,
                        color = UnknownOrange
                    )
                }
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Close, contentDescription = "Rimuovi", tint = Color.LightGray)
            }
        }
    }
}
