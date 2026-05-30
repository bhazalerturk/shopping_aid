package com.esselunga.navigator.ui.caregiver

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esselunga.navigator.viewmodel.ShoppingViewModel

private val EasylungaGreen = Color(0xFF00843D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCaregiverFromLinkScreen(
    viewModel: ShoppingViewModel,
    initialName: String? = null,
    initialPhone: String? = null,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    var caregiverName by remember { mutableStateOf(initialName ?: "") }
    var caregiverPhone by remember { mutableStateOf(initialPhone ?: "") }
    var saved by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Set Up Caregiver", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
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
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("👤 Add Your Caregiver", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = EasylungaGreen)
                    Text(
                        "Your caregiver will be able to help you and receive notifications",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Form
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {

                    // Name field
                    OutlinedTextField(
                        value = caregiverName,
                        onValueChange = { caregiverName = it; saved = false; errorMessage = "" },
                        label = { Text("Caregiver Name") },
                        placeholder = { Text("e.g., Maria García") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )

                    // Phone field
                    OutlinedTextField(
                        value = caregiverPhone,
                        onValueChange = { caregiverPhone = it; saved = false; errorMessage = "" },
                        label = { Text("Phone Number") },
                        placeholder = { Text("e.g., +39 123 456 7890") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Error message
                    if (errorMessage.isNotEmpty()) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                errorMessage,
                                fontSize = 14.sp,
                                color = Color(0xFFC62828),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Save button
                    Button(
                        onClick = {
                            if (caregiverName.isBlank()) {
                                errorMessage = "Please enter caregiver's name"
                                return@Button
                            }
                            if (caregiverPhone.isBlank()) {
                                errorMessage = "Please enter phone number"
                                return@Button
                            }
                            viewModel.setCaregiverContact(caregiverName, caregiverPhone)
                            saved = true
                            onSaved()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EasylungaGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("✓ Save Caregiver", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    // Success message
                    if (saved) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                "✓ Caregiver saved successfully!",
                                fontSize = 14.sp,
                                color = Color(0xFF2E7D32),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Info box
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("ℹ️ Why add a caregiver?", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF57C00))
                    Text(
                        "• Call them for help while shopping\n• Share your shopping list with them\n• They can assist you anytime",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}
