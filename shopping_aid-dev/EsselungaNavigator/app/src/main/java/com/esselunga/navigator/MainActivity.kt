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
import androidx.compose.material3.Typography
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.esselunga.navigator.ui.budget.BudgetScreen
import com.esselunga.navigator.ui.help.HelpScreen
import com.esselunga.navigator.ui.home.HomeScreen
import com.esselunga.navigator.ui.list.ListScreen
import com.esselunga.navigator.ui.map.MapScreen
import com.esselunga.navigator.ui.navigation.NavigationScreen
import com.esselunga.navigator.ui.review.ReviewScreen
import com.esselunga.navigator.ui.wizard.WizardScreen
import com.esselunga.navigator.viewmodel.ShoppingViewModel

object Routes {
    const val HOME = "home"
    const val BUDGET = "budget"
    const val WIZARD = "wizard"
    const val LIST = "list"
    const val REVIEW = "review"
    const val NAVIGATION = "navigation"
    const val MAP = "map"
    const val HELP = "help"
}

class MainActivity : ComponentActivity() {

    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        setContent {
            val typography = Typography(
                bodyLarge = TextStyle(fontSize = 18.sp),
                bodyMedium = TextStyle(fontSize = 16.sp),
                bodySmall = TextStyle(fontSize = 14.sp)
            )
            MaterialTheme(typography = typography) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    EasylungaApp()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
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
fun EasylungaApp() {
    val navController: NavHostController = rememberNavController()
    val shoppingViewModel: ShoppingViewModel = viewModel()
    val nfcTrigger = MainActivity.nfcTrigger

    LaunchedEffect(nfcTrigger) {
        if (nfcTrigger > 0) {
            navController.navigate(Routes.BUDGET) {
                popUpTo(Routes.HOME) { inclusive = false }
            }
        }
    }

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onStart = { navController.navigate(Routes.BUDGET) },
                onHelp = { navController.navigate(Routes.HELP) }
            )
        }
        composable(Routes.BUDGET) {
            BudgetScreen(
                viewModel = shoppingViewModel,
                onNext = { navController.navigate(Routes.WIZARD) },
                onSkip = { navController.navigate(Routes.WIZARD) }
            )
        }
        composable(Routes.WIZARD) {
            WizardScreen(
                viewModel = shoppingViewModel,
                onDone = { navController.navigate(Routes.LIST) },
                onSkip = { navController.navigate(Routes.LIST) }
            )
        }
        composable(Routes.LIST) {
            ListScreen(
                viewModel = shoppingViewModel,
                onStartNavigation = { navController.navigate(Routes.NAVIGATION) },
                onReview = { navController.navigate(Routes.REVIEW) },
                onAddWithWizard = { navController.navigate(Routes.WIZARD) }
            )
        }
        composable(Routes.REVIEW) {
            ReviewScreen(
                viewModel = shoppingViewModel,
                onBack = { navController.popBackStack() },
                onGoShopping = { navController.navigate(Routes.NAVIGATION) }
            )
        }
        composable(Routes.NAVIGATION) {
            NavigationScreen(
                viewModel = shoppingViewModel,
                onBack = { navController.popBackStack() },
                onOpenMap = { navController.navigate(Routes.MAP) },
                onHelp = { navController.navigate(Routes.HELP) }
            )
        }
        composable(Routes.MAP) {
            MapScreen(
                viewModel = shoppingViewModel,
                onBack = { navController.popBackStack() },
                onHelp = { navController.navigate(Routes.HELP) }
            )
        }
        composable(Routes.HELP) {
            HelpScreen(
                viewModel = shoppingViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
