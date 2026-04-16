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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esselunga.navigator.util.RouteStep
import com.esselunga.navigator.viewmodel.ShoppingViewModel

private val EsselungaGreen = Color(0xFF00843D)
private val AisleBlue = Color(0xFF1565C0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationScreen(
    viewModel: ShoppingViewModel,
    onBack: () -> Unit,
    onOpenMap: () -> Unit
) {
    val route = remember(viewModel.items.collectAsState().value) {
        viewModel.route
    }

    var currentStepIndex by remember { mutableIntStateOf(0) }
    val currentStep = route.getOrNull(currentStepIndex)
    val progress = if (route.isEmpty()) 1f else (currentStepIndex.toFloat() / route.size)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Navigazione", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EsselungaGreen,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = onOpenMap) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Mappa", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = EsselungaGreen,
                trackColor = Color(0xFFE0E0E0)
            )

            // Current step hero card
            if (currentStep != null) {
                CurrentStepCard(
                    step = currentStep,
                    stepNumber = currentStepIndex + 1,
                    total = route.size,
                    onNext = {
                        if (currentStepIndex < route.size - 1) currentStepIndex++
                        // If PickItem step, mark as checked
                        if (currentStep is RouteStep.PickItem) {
                            viewModel.toggleChecked(currentStep.item.id)
                        }
                    },
                    isLast = currentStepIndex == route.size - 1
                )
            } else {
                DoneCard(onBack = onBack)
            }

            // Upcoming steps list
            Text(
                "Prossimi step",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                itemsIndexed(route.drop(currentStepIndex + 1).take(6)) { idx, step ->
                    UpcomingStepRow(step = step, index = currentStepIndex + 1 + idx + 1)
                }
            }
        }
    }
}

@Composable
private fun CurrentStepCard(
    step: RouteStep,
    stepNumber: Int,
    total: Int,
    onNext: () -> Unit,
    isLast: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EsselungaGreen),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Step $stepNumber di $total",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp
            )

            when (step) {
                is RouteStep.GoToAisle -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(56.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "${step.corsia}",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("Vai alla corsia", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                            Text("Corsia ${step.corsia}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                            Text(step.sectionLabel, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                        }
                    }
                }
                is RouteStep.PickItem -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("Prendi", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                            Text(step.item.rawText, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            step.item.category?.let {
                                Text("Corsia ${it.corsia}", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                            }
                        }
                    }
                }
                is RouteStep.AskStaff -> {
                    Text("Chiedi al personale per:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    step.items.forEach {
                        Text("• ${it.rawText}", color = Color.White.copy(alpha = 0.9f))
                    }
                }
            }

            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    if (isLast) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = EsselungaGreen
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    if (isLast) "Fatto!" else "Avanti",
                    color = EsselungaGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DoneCard(onBack: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = EsselungaGreen, modifier = Modifier.size(56.dp))
            Text("Lista completata!", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = EsselungaGreen)
            Text("Buona spesa!", color = Color.Gray)
            Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = EsselungaGreen)) {
                Text("Nuova lista")
            }
        }
    }
}

@Composable
private fun UpcomingStepRow(step: RouteStep, index: Int) {
    val (label, sub) = when (step) {
        is RouteStep.GoToAisle -> "Corsia ${step.corsia}" to step.sectionLabel
        is RouteStep.PickItem -> step.item.rawText to (step.item.category?.let { "Corsia ${it.corsia}" } ?: "")
        is RouteStep.AskStaff -> "Chiedi al personale" to "${step.items.size} prodotti"
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("$index", color = Color.Gray, fontSize = 13.sp, modifier = Modifier.width(24.dp))
        Spacer(Modifier.width(8.dp))
        Column {
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            if (sub.isNotEmpty()) Text(sub, fontSize = 12.sp, color = Color.Gray)
        }
    }
}
