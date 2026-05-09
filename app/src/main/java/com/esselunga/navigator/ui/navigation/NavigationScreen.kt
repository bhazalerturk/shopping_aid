package com.esselunga.navigator.ui.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esselunga.navigator.util.BudgetCalculator
import com.esselunga.navigator.util.RouteStep
import com.esselunga.navigator.viewmodel.ShoppingViewModel

private val EasylungaGreen = Color(0xFF00843D)
private val AisleBlue = Color(0xFF1565C0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationScreen(
    viewModel: ShoppingViewModel,
    onBack: () -> Unit,
    onOpenMap: () -> Unit,
    onHelp: () -> Unit
) {
    val route = remember(viewModel.items.collectAsState().value) { viewModel.route }
    val budget by viewModel.budget.collectAsState()

    var currentStepIndex by remember { mutableIntStateOf(0) }
    val currentStep = route.getOrNull(currentStepIndex)
    val progress = if (route.isEmpty()) 1f else (currentStepIndex.toFloat() / route.size)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Navigation", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
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
                    IconButton(onClick = onOpenMap) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Map", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Progress bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Step ${currentStepIndex + 1} of ${route.size}", fontSize = 14.sp, color = Color.Gray)
                    if (budget > 0) {
                        Text(
                            "Spent: ${BudgetCalculator.formatEuro(viewModel.totalCost)}",
                            fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = EasylungaGreen
                        )
                    }
                }
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(10.dp),
                    color = EasylungaGreen, trackColor = Color(0xFFE0E0E0)
                )
            }

            // Current step hero card
            if (currentStep != null) {
                CurrentStepCard(
                    step = currentStep,
                    stepNumber = currentStepIndex + 1,
                    total = route.size,
                    onNext = {
                        if (currentStep is RouteStep.PickItem) {
                            viewModel.toggleChecked(currentStep.item.id)
                        }
                        if (currentStepIndex < route.size - 1) currentStepIndex++
                    },
                    isLast = currentStepIndex == route.size - 1
                )
            } else {
                DoneCard(onBack = onBack)
            }

            // Upcoming steps
            Text("Coming up:", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)

            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                itemsIndexed(route.drop(currentStepIndex + 1).take(5)) { idx, step ->
                    UpcomingStepRow(step = step, index = currentStepIndex + 2 + idx)
                }
            }
        }
    }
}

// Map section label to a fun color for the step card
private fun stepCardColor(step: RouteStep): Color = when (step) {
    is RouteStep.GoToAisle -> Color(0xFF1565C0)   // deep blue for navigation
    is RouteStep.PickItem  -> when (null) {
        "PRODUCE"       -> Color(0xFF2E7D32)
        "BAKERY"        -> Color(0xFFE65100)
        "PASTA_RICE"    -> Color(0xFFF9A825)
        "CONDIMENTS"    -> Color(0xFF6A1B9A)
        "DAIRY"         -> Color(0xFF0277BD)
        "DELI"          -> Color(0xFFC62828)
        "MEAT"          -> Color(0xFFBF360C)
        "FROZEN"        -> Color(0xFF00838F)
        "BREAKFAST"     -> Color(0xFFD84315)
        "DRINKS"        -> Color(0xFF1565C0)
        "PERSONAL_CARE" -> Color(0xFF6A1B9A)
        "CLEANING"      -> Color(0xFF37474F)
        "PET"           -> Color(0xFF4E342E)
        else            -> Color(0xFF00843D)
    }
    is RouteStep.AskStaff  -> Color(0xFFF57C00)
}

@Composable
private fun CurrentStepCard(step: RouteStep, stepNumber: Int, total: Int, onNext: () -> Unit, isLast: Boolean) {
    val cardColor = stepCardColor(step)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            when (step) {
                is RouteStep.GoToAisle -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(64.dp).background(Color.White.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${step.corsia}", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("🚶 Walk to", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)
                            Text("Aisle ${step.corsia}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 28.sp)
                            Text(step.sectionLabel, color = Color.White.copy(alpha = 0.8f), fontSize = 15.sp)
                        }
                    }
                }
                is RouteStep.PickItem -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            when (null) {
                                "PRODUCE" -> "🥦"; "BAKERY" -> "🍞"; "PASTA_RICE" -> "🍝"
                                "CONDIMENTS" -> "🫙"; "DAIRY" -> "🥛"; "DELI" -> "🥩"
                                "MEAT" -> "🍗"; "FROZEN" -> "🧊"; "BREAKFAST" -> "🥣"
                                "DRINKS" -> "🥤"; "PERSONAL_CARE" -> "🧴"
                                "CLEANING" -> "🧹"; "PET" -> "🐾"
                                else -> "🛒"
                            },
                            fontSize = 48.sp
                        )
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("🎯 Pick up", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)
                            Text(step.item.rawText, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                step.item.product?.let { Text("Aisle ${null}", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp) }
                                if (step.item.quantity > 1) Text("x${step.item.quantity}", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                if (step.item.totalPrice > 0) Text(BudgetCalculator.formatEuro(step.item.totalPrice), color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                            }
                        }
                    }
                }
                is RouteStep.AskStaff -> {
                    Text("🙋 Ask a staff member for:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    step.items.forEach {
                        Text("• ${it.rawText}", color = Color.White.copy(alpha = 0.9f), fontSize = 17.sp)
                    }
                }
            }

            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    if (isLast) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null, tint = cardColor, modifier = Modifier.size(26.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    if (isLast) "Done! ✓" else "Next →",
                    color = cardColor, fontWeight = FontWeight.Bold, fontSize = 20.sp
                )
            }
        }
    }
}

@Composable
private fun DoneCard(onBack: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
    ) {
        Column(
            modifier = Modifier.padding(28.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("🎉", fontSize = 64.sp)
            Text("Shopping complete!", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = EasylungaGreen)
            Text("Great job! You got everything.", color = Color.Gray, fontSize = 16.sp)
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EasylungaGreen),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Start a new list", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun UpcomingStepRow(step: RouteStep, index: Int) {
    val (label, sub) = when (step) {
        is RouteStep.GoToAisle -> "Aisle ${step.corsia}" to step.sectionLabel
        is RouteStep.PickItem -> step.item.rawText to (step.item.product?.let { "Aisle ${null}" } ?: "")
        is RouteStep.AskStaff -> "Ask staff" to "${step.items.size} products"
    }
    Row(
        modifier = Modifier.fillMaxWidth().background(Color(0xFFF5F5F5), RoundedCornerShape(10.dp)).padding(14.dp, 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("$index", color = Color.LightGray, fontSize = 14.sp, modifier = Modifier.width(28.dp))
        Spacer(Modifier.width(8.dp))
        Column {
            Text(label, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            if (sub.isNotEmpty()) Text(sub, fontSize = 13.sp, color = Color.Gray)
        }
    }
}
