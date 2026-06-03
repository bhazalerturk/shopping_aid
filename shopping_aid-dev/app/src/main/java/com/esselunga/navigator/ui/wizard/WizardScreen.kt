package com.esselunga.navigator.ui.wizard

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esselunga.navigator.viewmodel.ShoppingViewModel

private val Green      = Color(0xFF00843D)
private val Green800   = Color(0xFF1B4332)
private val GreenLight = Color(0xFFE8F5E9)

// ─────────────────────────────────────────────────────────────────────────────
// Root
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun WizardScreen(
    viewModel: ShoppingViewModel,
    onDone: () -> Unit,
    onSkip: () -> Unit
) {
    var step by remember { mutableIntStateOf(0) }
    val days   by viewModel.wizardDays.collectAsState()
    val people by viewModel.wizardPeople.collectAsState()
    var budget by remember { mutableIntStateOf(50) }

    val totalSteps = 4

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenLight)
    ) {
        // Progress dots
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 32.dp, end = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(totalSteps) { i ->
                Box(
                    modifier = Modifier
                        .size(if (i == step) 14.dp else 10.dp)
                        .clip(CircleShape)
                        .background(if (i <= step) Green else Color.LightGray)
                )
                if (i < totalSteps - 1) Spacer(Modifier.width(8.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        when (step) {
            0 -> ListStep(
                onNext = { step = 1 }
            )
            1 -> BudgetStep(
                budget   = budget,
                onChange = { budget = it },
                onNext   = { viewModel.setBudget(budget.toDouble()); step = 2 },
                onBack   = { step = 0 },
                onSkip   = { step = 2 }
            )
            2 -> DaysStep(
                selectedDays = days,
                onSelect     = { viewModel.setWizardDays(it) },
                onNext       = { step = 3 },
                onBack       = { step = 1 },
                onSkip       = { step = 3 }
            )
            3 -> PeopleStep(
                selectedPeople = people,
                onSelect       = { viewModel.setWizardPeople(it) },
                onNext         = onDone,
                onBack         = { step = 2 },
                onSkip         = onDone
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared nav buttons
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun WizardNav(
    onBack:    (() -> Unit)? = null,
    onNext:    () -> Unit,
    onSkip:    (() -> Unit)? = null,
    nextLabel: String = "Next"
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (onBack != null) {
                OutlinedButton(
                    onClick  = onBack,
                    modifier = Modifier.weight(1f).height(64.dp),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = Green800),
                    border   = ButtonDefaults.outlinedButtonBorder
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Back", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Button(
                onClick  = onNext,
                modifier = Modifier.weight(1f).height(64.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Green)
            ) {
                Text(nextLabel, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                if (nextLabel == "Next") {
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(22.dp))
                }
            }
        }

        if (onSkip != null) {
            TextButton(
                onClick  = onSkip,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Skip", fontSize = 15.sp, color = Color.Gray)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Step 0 — List creation (FIRST STEP NOW)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ListStep(
    onNext: () -> Unit
) {
    var showSendLinkDialog by remember { mutableStateOf(false) }

    if (showSendLinkDialog) {
        SendLinkDialog(
            onDismiss  = { showSendLinkDialog = false },
            onSendLink = { showSendLinkDialog = false }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("📝", fontSize = 64.sp)
            Text(
                "How do you want to make the list?",
                fontSize = 26.sp, fontWeight = FontWeight.Bold,
                color = Green, textAlign = TextAlign.Center
            )
            Text(
                "You can always make it yourself and then send it to others",
                fontSize = 15.sp, color = Color.Gray, textAlign = TextAlign.Center
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick  = onNext,
                modifier = Modifier.fillMaxWidth().height(64.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Green, contentColor = Color.White),
                shape    = RoundedCornerShape(14.dp),
                elevation = ButtonDefaults.buttonElevation(2.dp)
            ) {
                Text("Make the list by myself", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }

            OutlinedButton(
                onClick  = { showSendLinkDialog = true },
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape    = RoundedCornerShape(14.dp),
                border   = ButtonDefaults.outlinedButtonBorder
            ) {
                Text(
                    "Send link for someone to create it",
                    fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Green
                )
            }
        }

        WizardNav(onNext = onNext)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Step 1 — Budget
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun BudgetStep(
    budget:   Int,
    onChange: (Int) -> Unit,
    onNext:   () -> Unit,
    onBack:   () -> Unit,
    onSkip:   () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("💶", fontSize = 64.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                "How much do you want to spend?",
                fontSize = 26.sp, fontWeight = FontWeight.Bold,
                color = Green, textAlign = TextAlign.Center
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "€$budget",
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = Green800
            )
            Spacer(Modifier.height(8.dp))
            Slider(
                value         = budget.toFloat(),
                onValueChange = { onChange(it.toInt()) },
                valueRange    = 1f..100f,
                steps         = 98,
                modifier      = Modifier.fillMaxWidth(),
                colors        = SliderDefaults.colors(
                    thumbColor          = Green,
                    activeTrackColor    = Green,
                    inactiveTrackColor  = Color.LightGray
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("€1", fontSize = 13.sp, color = Color.Gray)
                Text("€100", fontSize = 13.sp, color = Color.Gray)
            }
        }

        WizardNav(onBack = onBack, onNext = onNext, onSkip = onSkip)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Step 2 — Days
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DaysStep(
    selectedDays: Int,
    onSelect: (Int) -> Unit,
    onNext:   () -> Unit,
    onBack:   () -> Unit,
    onSkip:   () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("📅", fontSize = 64.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                "For how many days?",
                fontSize = 26.sp, fontWeight = FontWeight.Bold,
                color = Green, textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "I am shopping for...",
                fontSize = 16.sp, color = Color.Gray, textAlign = TextAlign.Center
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            listOf(1 to "1 day", 3 to "2–3 days", 7 to "A week").forEach { (d, label) ->
                Button(
                    onClick  = { onSelect(d) },
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = if (selectedDays == d) Green else Color.White,
                        contentColor   = if (selectedDays == d) Color.White else Green
                    ),
                    shape     = RoundedCornerShape(14.dp),
                    elevation = ButtonDefaults.buttonElevation(2.dp)
                ) {
                    Text(label, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        WizardNav(onBack = onBack, onNext = onNext, onSkip = onSkip)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Step 3 — People (LAST STEP NOW)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun PeopleStep(
    selectedPeople: Int,
    onSelect: (Int) -> Unit,
    onNext:   () -> Unit,
    onBack:   () -> Unit,
    onSkip:   () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("👥", fontSize = 64.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                "For how many people?",
                fontSize = 26.sp, fontWeight = FontWeight.Bold,
                color = Green, textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "I am shopping for...",
                fontSize = 16.sp, color = Color.Gray, textAlign = TextAlign.Center
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            listOf(1 to "Just me", 2 to "2 people", 3 to "3 people", 4 to "4+ people").forEach { (p, label) ->
                Button(
                    onClick  = { onSelect(p) },
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = if (selectedPeople == p) Green else Color.White,
                        contentColor   = if (selectedPeople == p) Color.White else Green
                    ),
                    shape     = RoundedCornerShape(14.dp),
                    elevation = ButtonDefaults.buttonElevation(2.dp)
                ) {
                    Text(label, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        WizardNav(onBack = onBack, onNext = onNext, onSkip = onSkip, nextLabel = "Done ✓")
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Send-link dialog
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SendLinkDialog(
    onDismiss:  () -> Unit,
    onSendLink: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("📤 Share Caregiver Link", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Send this link to someone so they can create the shopping list for you:",
                    fontSize = 14.sp, color = Color.Gray
                )
                Card(
                    colors   = CardDefaults.cardColors(containerColor = GreenLight),
                    shape    = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.fillMaxWidth().padding(12.dp)) {
                        Text("Link:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Green)
                        Spacer(Modifier.height(4.dp))
                        Text("shoppingaid://create-caregiver", fontSize = 12.sp, color = Color.DarkGray)
                    }
                }
                Text("✓ They'll be able to add items to your shopping list", fontSize = 13.sp, color = Color.DarkGray)
                Text("✓ You can review everything before you start shopping", fontSize = 13.sp, color = Color.DarkGray)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val shareText = "Help me create a shopping list!\n\nshoppingaid://create-caregiver"
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(intent, "Share Caregiver Link"))
                    onSendLink()
                },
                colors   = ButtonDefaults.buttonColors(containerColor = Green),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Share Link", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Green) }
        },
        shape          = RoundedCornerShape(14.dp),
        containerColor = Color.White
    )
}