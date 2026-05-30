package com.esselunga.navigator.ui.help

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esselunga.navigator.viewmodel.ShoppingViewModel

private val EasylungaGreen = Color(0xFF00843D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    viewModel: ShoppingViewModel,
    onBack: () -> Unit
) {
    val caregiver by viewModel.caregiver.collectAsState()
    val context = LocalContext.current

    var caregiverName by remember { mutableStateOf(caregiver?.name ?: "") }
    var caregiverPhone by remember { mutableStateOf(caregiver?.phoneNumber ?: "") }
    var showStaffMessage by remember { mutableStateOf(false) }
    var saved by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EasylungaGreen, titleContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Call caregiver
            if (caregiver != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("📞 Call My Caregiver", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = EasylungaGreen)
                        Text("${caregiver!!.name} · ${caregiver!!.phoneNumber}", fontSize = 15.sp, color = Color.DarkGray)
                        Button(
                            onClick = {
                                viewModel.callCaregiverIntent()?.let { context.startActivity(it) }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EasylungaGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("📞 Call Now", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Ask for help in store
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("🏪 Ask for help in store", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF57C00))
                    Text("Show this message to a store employee", fontSize = 14.sp, color = Color.Gray)
                    Button(
                        onClick = { showStaffMessage = !showStaffMessage },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57C00)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (showStaffMessage) "Hide message" else "Show message to staff", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    if (showStaffMessage) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(10.dp))
                                .padding(16.dp)
                        ) {
                            Text(
                                "Hello, I need help finding a product in the store.\nCould you please help me?",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                lineHeight = 30.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Caregiver setup
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("👤 Caregiver Contact", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = caregiverName,
                        onValueChange = { caregiverName = it; saved = false },
                        label = { Text("Caregiver name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = caregiverPhone,
                        onValueChange = { caregiverPhone = it; saved = false },
                        label = { Text("Phone number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Button(
                        onClick = {
                            if (caregiverName.isNotBlank() && caregiverPhone.isNotBlank()) {
                                viewModel.setCaregiverContact(caregiverName, caregiverPhone)
                                saved = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (saved) Color(0xFF388E3C) else EasylungaGreen
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (saved) "✓ Saved!" else "Save Caregiver", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    if (caregiver != null) {
                        TextButton(
                            onClick = { viewModel.clearCaregiverContact(); caregiverName = ""; caregiverPhone = ""; saved = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Remove caregiver", color = Color.Red, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
