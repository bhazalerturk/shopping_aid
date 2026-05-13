package com.esselunga.navigator.ui.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esselunga.navigator.viewmodel.ShoppingViewModel

private val EasylungaGreen = Color(0xFF00843D)
private val EasylungaLightGreen = Color(0xFFE8F5E9)

@Composable
fun BudgetScreen(
    viewModel: ShoppingViewModel,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    var selectedBudget by remember { mutableDoubleStateOf(20.0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EasylungaLightGreen)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(40.dp))
            Text(
                text = "💰",
                fontSize = 64.sp
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "How much do you\nwant to spend?",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = EasylungaGreen,
                textAlign = TextAlign.Center,
                lineHeight = 34.sp
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Big amount display
            Text(
                text = "€%.0f".format(selectedBudget),
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = EasylungaGreen
            )

            // Preset buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf(10.0, 20.0, 30.0, 50.0).forEach { amount ->
                    Button(
                        onClick = { selectedBudget = amount },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedBudget == amount) EasylungaGreen else Color.White,
                            contentColor = if (selectedBudget == amount) Color.White else EasylungaGreen
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(2.dp)
                    ) {
                        Text("€${amount.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Slider
            Slider(
                value = selectedBudget.toFloat(),
                onValueChange = { selectedBudget = it.toDouble() },
                valueRange = 5f..100f,
                steps = 18,
                colors = SliderDefaults.colors(thumbColor = EasylungaGreen, activeTrackColor = EasylungaGreen),
                modifier = Modifier.fillMaxWidth()
            )
            Text("Drag to set a custom amount", fontSize = 14.sp, color = Color.Gray)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    viewModel.setBudget(selectedBudget)
                    onNext()
                },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EasylungaGreen),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Next →", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            TextButton(onClick = onSkip) {
                Text("Skip budget", fontSize = 16.sp, color = Color.Gray)
            }
        }
    }
}
