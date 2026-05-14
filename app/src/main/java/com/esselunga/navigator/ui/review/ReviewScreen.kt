package com.esselunga.navigator.ui.review

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esselunga.navigator.util.BudgetCalculator
import com.esselunga.navigator.viewmodel.ShoppingViewModel

private val EasylungaGreen = Color(0xFF00843D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    viewModel: ShoppingViewModel,
    onBack: () -> Unit,
    onGoShopping: () -> Unit
) {
    val items by viewModel.items.collectAsState()
    val budget by viewModel.budget.collectAsState()
    val caregiver by viewModel.caregiver.collectAsState()
    val context = LocalContext.current

    var showCaregiverSetup by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Review Your List", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EasylungaGreen, titleContentColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Summary card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("${items.size} items", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = EasylungaGreen)
                            if (budget > 0) {
                                Text(
                                    "Budget left: ${BudgetCalculator.formatEuro(viewModel.budgetRemaining)}",
                                    fontSize = 14.sp, color = Color.DarkGray
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Total", fontSize = 13.sp, color = Color.Gray)
                            Text(
                                BudgetCalculator.formatEuro(viewModel.totalCost),
                                fontSize = 22.sp, fontWeight = FontWeight.Bold,
                                color = if (budget > 0 && viewModel.totalCost > budget) Color.Red else EasylungaGreen
                            )
                        }
                    }
                }
            }

            // Caregiver section
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Share with caregiver", fontSize = 17.sp, fontWeight = FontWeight.Bold)

                        Text("${caregiver!!.name} · ${caregiver!!.phoneNumber}", fontSize = 14.sp, color = Color.Gray)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = {
                                    val listId = "current"
                                    val shareLink = "shoppingaid://caregiver-list?listId=$listId"
                                    val shareText = "Click here to help me making mi shopping list:\n$shareLink"
                                    val sendIntent = android.content.Intent().apply {
                                        action = android.content.Intent.ACTION_SEND
                                        putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                                        type = "text/plain"
                                    }
                                    context.startActivity(android.content.Intent.createChooser(sendIntent, "Share Link")) },
                                    modifier = Modifier.weight(1f).height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EasylungaGreen),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Send Link", fontSize = 15.sp)
                                }
                                OutlinedButton(
                                    onClick = { showCaregiverSetup = !showCaregiverSetup },
                                    modifier = Modifier.height(50.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Edit", fontSize = 15.sp)
                                }
                            }
                    }
                }
            }

            // Items
            item { Text("Your items:", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray) }

            items(items) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(12.dp, 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(item.rawText, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            item.category?.let { cat ->
                                Text("Aisle ${cat.corsia} · ${cat.section.label}", fontSize = 12.sp, color = EasylungaGreen)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("x${item.quantity}", fontSize = 13.sp, color = Color.Gray)
                            Text(BudgetCalculator.formatEuro(item.totalPrice), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = EasylungaGreen)
                        }
                    }
                }
            }

            // Go shopping button
            item {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = onGoShopping,
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EasylungaGreen),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("🛒 Go Shopping!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
