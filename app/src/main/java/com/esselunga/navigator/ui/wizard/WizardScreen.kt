package com.esselunga.navigator.ui.wizard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esselunga.navigator.viewmodel.ShoppingViewModel

private val EasylungaGreen = Color(0xFF00843D)
private val EasylungaLightGreen = Color(0xFFE8F5E9)

@Composable
fun WizardScreen(
    viewModel: ShoppingViewModel,
    onDone: () -> Unit,
    onSkip: () -> Unit
) {
    var step by remember { mutableIntStateOf(0) }
    val days by viewModel.wizardDays.collectAsState()
    val people by viewModel.wizardPeople.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EasylungaLightGreen)
    ) {
        // Step dots (2 steps)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp, 32.dp, 24.dp, 0.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(2) { i ->
                Box(
                    modifier = Modifier
                        .size(if (i == step) 14.dp else 10.dp)
                        .clip(CircleShape)
                        .background(if (i <= step) EasylungaGreen else Color.LightGray)
                )
                if (i < 1) Spacer(Modifier.width(8.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        when (step) {
            0 -> DaysStep(
                selectedDays = days,
                onSelect = { viewModel.setWizardDays(it) },
                onNext = { step = 1 },
                onSkip = onSkip
            )
            1 -> PeopleStep(
                selectedPeople = people,
                days = days,
                onSelect = { viewModel.setWizardPeople(it) },
                onNext = onDone,
                onBack = { step = 0 }
            )
        }
    }
}

@Composable
private fun DaysStep(
    selectedDays: Int,
    onSelect: (Int) -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text("📅", fontSize = 64.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                "For how many days?",
                fontSize = 26.sp, fontWeight = FontWeight.Bold,
                color = EasylungaGreen, textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text("I am shopping for...", fontSize = 16.sp, color = Color.Gray, textAlign = TextAlign.Center)
        }

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            listOf(1 to "1 day", 3 to "2–3 days", 7 to "A week").forEach { (d, label) ->
                Button(
                    onClick = { onSelect(d) },
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedDays == d) EasylungaGreen else Color.White,
                        contentColor = if (selectedDays == d) Color.White else EasylungaGreen
                    ),
                    shape = RoundedCornerShape(14.dp),
                    elevation = ButtonDefaults.buttonElevation(2.dp)
                ) {
                    Text(label, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EasylungaGreen),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Next →", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            TextButton(onClick = onSkip, modifier = Modifier.fillMaxWidth()) {
                Text("Skip — I'll add items manually", fontSize = 15.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun PeopleStep(
    selectedPeople: Int,
    days: Int,
    onSelect: (Int) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text("👥", fontSize = 64.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                "For how many people?",
                fontSize = 26.sp, fontWeight = FontWeight.Bold,
                color = EasylungaGreen, textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text("We'll suggest quantities as you add products", fontSize = 15.sp, color = Color.Gray, textAlign = TextAlign.Center)
        }

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            listOf(1 to "Just me", 2 to "2 people", 3 to "3 people", 4 to "4+ people").forEach { (p, label) ->
                Button(
                    onClick = { onSelect(p) },
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedPeople == p) EasylungaGreen else Color.White,
                        contentColor = if (selectedPeople == p) Color.White else EasylungaGreen
                    ),
                    shape = RoundedCornerShape(14.dp),
                    elevation = ButtonDefaults.buttonElevation(2.dp)
                ) {
                    Text(label, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Preview hint
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Row(
                Modifier.fillMaxWidth().padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("💡", fontSize = 24.sp)
                Spacer(Modifier.width(10.dp))
                Text(
                    "Shopping for $selectedPeople ${if (selectedPeople == 1) "person" else "people"} for $days ${if (days == 1) "day" else "days"} — we'll suggest the right amounts!",
                    fontSize = 14.sp, color = Color.DarkGray, lineHeight = 20.sp
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("← Back", fontSize = 16.sp)
            }
            Button(
                onClick = onNext,
                modifier = Modifier.weight(2f).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EasylungaGreen),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Build My List →", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
