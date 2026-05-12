package com.esselunga.navigator.ui.wizard

import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
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
        // Step dots (3 steps)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp, 32.dp, 24.dp, 0.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(3) { i ->
                Box(
                    modifier = Modifier
                        .size(if (i == step) 14.dp else 10.dp)
                        .clip(CircleShape)
                        .background(if (i <= step) EasylungaGreen else Color.LightGray)
                )
                if (i < 2) Spacer(Modifier.width(8.dp))
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
                onNext = { step = 2 },
                onBack = { step = 0 }
            )
            2 -> ListStep(
                onNext = onDone,
                onBack = { step = 1 }
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
                Text("Next →", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ListStep(
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    var showSendLinkDialog by remember { mutableStateOf(false) }

    if (showSendLinkDialog) {
        SendLinkDialog(
            onDismiss = { showSendLinkDialog = false },
            onSendLink = { showSendLinkDialog = false }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("📝", fontSize = 64.sp)
            Text(
                "How do you want to make the list?",
                fontSize = 26.sp, fontWeight = FontWeight.Bold,
                color = EasylungaGreen, textAlign = TextAlign.Center
            )
            Text("You can always make it yourself and then send it to others", fontSize = 15.sp, color = Color.Gray, textAlign = TextAlign.Center)
        }

        Spacer(Modifier.height(32.dp))

        Column(verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth(0.9f)) {
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EasylungaGreen,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp),
                elevation = ButtonDefaults.buttonElevation(2.dp)
            ) {
                Text("Make the list by myself", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }

            OutlinedButton(
                onClick = { showSendLinkDialog = true },
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape = RoundedCornerShape(14.dp),
                border = ButtonDefaults.outlinedButtonBorder
            ) {
                Text("Send link for someone to create it", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = EasylungaGreen)
            }
        }
    }
}

@Composable
private fun SendLinkDialog(
    onDismiss: () -> Unit,
    onSendLink: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("📤 Share Caregiver Link", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Send this link to someone so they can create the shopping list for you:", fontSize = 14.sp, color = Color.Gray)

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.fillMaxWidth().padding(12.dp)) {
                        Text("Link:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EasylungaGreen)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "shoppingaid://create-caregiver",
                            fontSize = 12.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Text("✓ They'll be able to add items to your shopping list", fontSize = 13.sp, color = Color.DarkGray)
                Text("✓ You can review everything before you start shopping", fontSize = 13.sp, color = Color.DarkGray)
            }
        },
        confirmButton = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        val shareLink = "shoppingaid://create-caregiver"
                        val shareText = "Help me create a shopping list!\n\n$shareLink"

                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Caregiver Link"))
                        onSendLink()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EasylungaGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Share Link", color = Color.White)
                }

                // Provisional button for trials
                Button(
                    onClick = {
                        val uri = android.net.Uri.parse("shoppingaid://create-caregiver?name=Test&phone=%2B39123456789")
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri)
                        context.startActivity(intent)
                        onSendLink()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🧪 Provisional button for trials", color = Color.White, fontSize = 13.sp)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = EasylungaGreen)
            }
        },
        shape = RoundedCornerShape(14.dp),
        containerColor = Color.White
    )
}
