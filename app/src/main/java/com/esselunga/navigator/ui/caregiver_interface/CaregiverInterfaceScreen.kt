package com.esselunga.navigator.ui.caregiver_interface

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esselunga.navigator.viewmodel.ShoppingViewModel
import com.esselunga.navigator.ui.list.ListScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaregiverInterfaceScreen(
    viewModel: ShoppingViewModel,
    listId: String = "current",
    onListReady: () -> Unit
) {
    val context = LocalContext.current
    // If the caregiver is already saved in the app, prefill the fields
    val savedCaregiver by viewModel.caregiver.collectAsState()
    var caregiverName by remember { mutableStateOf(savedCaregiver?.name ?: "") }
    var caregiverPhone by remember { mutableStateOf(savedCaregiver?.phoneNumber ?: "") }
    var showList by remember { mutableStateOf(false) }

    if (!showList) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Caregiver Setup", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))
            OutlinedTextField(
                value = caregiverName,
                onValueChange = { caregiverName = it },
                label = { Text("Your Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = caregiverPhone,
                onValueChange = { caregiverPhone = it },
                label = { Text("Your Phone Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    // Save caregiver locally and show the list editing UI
                    if (caregiverName.isNotBlank() && caregiverPhone.isNotBlank()) {
                        viewModel.setCaregiverContact(caregiverName, caregiverPhone)
                        showList = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue to List", fontSize = 16.sp)
            }

            Spacer(Modifier.height(12.dp))

            // Provisional button to open the same deep link (for testing)
            OutlinedButton(
                onClick = {
                    val uri = Uri.parse("shoppingaid://caregiver-list?listId=$listId")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🧪 Open via deep link (provisional)")
            }
        }
    } else {
        // Show the editable list UI for the caregiver. Reuse ListScreen but with no-op navigation callbacks.
        Box(Modifier.fillMaxSize()) {
            ListScreen(
                viewModel = viewModel,
                onStartNavigation = { /* caregiver shouldn't start navigation from this interface */ },
                onReview = { /* could reuse review if desired */ },
                onAddWithWizard = { /* noop */ }
            )

            // Done button so the caregiver can signal they're finished editing
            Button(
                onClick = {
                    onListReady()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text("Done")
            }
        }
    }
}
