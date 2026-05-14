package com.esselunga.navigator.ui.caregiver_interface

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.esselunga.navigator.ui.list.ListScreen
import com.esselunga.navigator.viewmodel.ShoppingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaregiverInterfaceScreen(
    viewModel: ShoppingViewModel,
    listId: String = "current",
    onListReady: () -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        ListScreen(
            viewModel = viewModel,
            onStartNavigation = { /* caregiver shouldn't start navigation from this interface */ },
            onReview = { /* noop */ },
            onAddWithWizard = { /* noop */ },
            isCaregiverMode = true,
            onCaregiverDone = onListReady
        )
    }
}
