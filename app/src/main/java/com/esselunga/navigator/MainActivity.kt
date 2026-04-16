package com.esselunga.navigator

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.esselunga.navigator.ui.home.HomeScreen
import com.esselunga.navigator.ui.list.ListScreen
import com.esselunga.navigator.ui.map.MapScreen
import com.esselunga.navigator.ui.navigation.NavigationScreen
import com.esselunga.navigator.viewmodel.ShoppingViewModel

object Routes {
    const val HOME = "home"
    const val LIST = "list"
    const val NAVIGATION = "navigation"
    const val MAP = "map"
}

class MainActivity : ComponentActivity() {

    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    EsselungaApp()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Enable foreground NFC dispatch so the app intercepts NFC tags
        nfcAdapter?.let { adapter ->
            val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
            adapter.enableForegroundDispatch(this, pendingIntent, null, null)
        }
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // NFC tag detected → treat same as tapping the start button.
        // The NavController is held in the composable; we signal via a shared state.
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action ||
            NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            nfcTrigger++
        }
    }

    companion object {
        var nfcTrigger by mutableIntStateOf(0)
    }
}

@Composable
fun EsselungaApp() {
    val navController: NavHostController = rememberNavController()
    val shoppingViewModel: ShoppingViewModel = viewModel()
    val nfcTrigger = MainActivity.nfcTrigger

    // Navigate to list when NFC triggers
    LaunchedEffect(nfcTrigger) {
        if (nfcTrigger > 0) {
            navController.navigate(Routes.LIST) {
                popUpTo(Routes.HOME) { inclusive = false }
            }
        }
    }

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onStart = {
                    navController.navigate(Routes.LIST)
                }
            )
        }

        composable(Routes.LIST) {
            ListScreen(
                viewModel = shoppingViewModel,
                onStartNavigation = {
                    navController.navigate(Routes.NAVIGATION)
                }
            )
        }

        composable(Routes.NAVIGATION) {
            NavigationScreen(
                viewModel = shoppingViewModel,
                onBack = { navController.popBackStack() },
                onOpenMap = { navController.navigate(Routes.MAP) }
            )
        }

        composable(Routes.MAP) {
            MapScreen(
                viewModel = shoppingViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
