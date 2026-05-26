package com.esselunga.navigator.ui.list


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.esselunga.navigator.data.Product
import com.esselunga.navigator.data.ShoppingItem
import com.esselunga.navigator.data.StoreSection
import com.esselunga.navigator.util.BudgetCalculator
import com.esselunga.navigator.util.BudgetStatus
import com.esselunga.navigator.data.searchProducts
import com.esselunga.navigator.data.getCategoryById
import com.esselunga.navigator.data.getAveragePriceForProductType
import com.esselunga.navigator.data.isExpensiveForProductType
import com.esselunga.navigator.viewmodel.ShoppingViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImage
import androidx.compose.material.icons.filled.Info
import com.esselunga.navigator.data.ListDiff
import com.esselunga.navigator.data.ItemChange
import com.esselunga.navigator.data.ItemChangeType

private val EasylungaGreen = Color(0xFF00843D)
private val WarningYellow = Color(0xFFF9A825)
private val DangerRed = Color(0xFFD32F2F)
private val CaregiverPurple = Color(0xFF5E35B1)

// Fun color per category section
private fun sectionColor(section: StoreSection?): Color = when (section) {
    StoreSection.FRESHPRODUCTS      -> Color(0xFF43A047)
    StoreSection.BAKERY       -> Color(0xFFFF8F00)
    StoreSection.PASTA_RICE   -> Color(0xFFFBC02D)
    StoreSection.DISPENSA   -> Color(0xFF8E24AA)
    StoreSection.DAIRY        -> Color(0xFF0288D1)
    StoreSection.DELI         -> Color(0xFFD81B60)
    StoreSection.MEAT         -> Color(0xFFE53935)
    StoreSection.FROZEN       -> Color(0xFF00ACC1)
    StoreSection.BREAKFAST    -> Color(0xFFFF7043)
    StoreSection.DRINKS       -> Color(0xFF1E88E5)
    StoreSection.PERSONAL_CARE-> Color(0xFF7B1FA2)
    StoreSection.CLEANING     -> Color(0xFF546E7A)
    StoreSection.PET          -> Color(0xFF6D4C41)
    null                      -> Color(0xFFF57C00)
}



@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ListScreen(
    viewModel: ShoppingViewModel,
    onStartNavigation: () -> Unit,
    onReview: () -> Unit,
    onAddWithWizard: () -> Unit,
    isCaregiverMode: Boolean = false,
    onCaregiverDone: (() -> Unit)? = null,
    diff: ListDiff? = null
) {
    val items by viewModel.items.collectAsState()
    val budget by viewModel.budget.collectAsState()
    val totalCost by viewModel.totalCost.collectAsState()
    val days by viewModel.wizardDays.collectAsState()
    val people by viewModel.wizardPeople.collectAsState()
    val wizardActive = days > 1 || people > 1

    var inputText by remember { mutableStateOf("") }
    var showExpensiveDialog  by remember { mutableStateOf(false) }
    var pendingProductName   by remember { mutableStateOf("") }
    var pendingProduct       by remember { mutableStateOf<Product?>(null) }
    var pendingAvgPrice      by remember { mutableStateOf(0.0) }
    var pendingCategoryName  by remember { mutableStateOf("") }

    // ── Budget warning state ───────────────────────────────────────────────
    var showBudgetWarning        by remember { mutableStateOf(false) }
    var budgetWarningProductName by remember { mutableStateOf("") }
    var budgetWarningProduct     by remember { mutableStateOf<Product?>(null) }
    var budgetWarningRemaining   by remember { mutableStateOf(0.0) }
    var budgetWarningItemPrice   by remember { mutableStateOf(0.0) }

    // ── Quantity warning state ─────────────────────────────────────────────
    var showQuantityWarning       by remember { mutableStateOf(false) }
    var quantityWarningName       by remember { mutableStateOf("") }
    var quantityWarningRecommended by remember { mutableStateOf(0) }
    var quantityWarningCurrent    by remember { mutableStateOf(0) }
    var quantityPendingProductName by remember { mutableStateOf("") }
    var quantityPendingProduct    by remember { mutableStateOf<Product?>(null) }
    var quantityPendingIsIncrement by remember { mutableStateOf(false) }
    val quantityPendingItemId     by remember { mutableStateOf("") }
    val searchResults = remember(inputText) {
        if (inputText.length >= 2) {
            searchProducts(inputText)
        } else {
            emptyList()
        }
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val budgetStatus = BudgetCalculator.budgetStatus(totalCost, budget)
    val baseThemeColor = if (isCaregiverMode) CaregiverPurple else EasylungaGreen
    val progressColor by animateColorAsState(
        targetValue = when (budgetStatus) {
            BudgetStatus.OVER    -> DangerRed
            BudgetStatus.WARNING -> WarningYellow
            else                 -> baseThemeColor
        },
        animationSpec = tween(400), label = "budgetColor"
    )

    val checkedCount = items.count { it.checked }
    val totalCount = items.size

    fun tryAddWithCheck(productName: String, product: Product?) {
        if (productName.isBlank()) return

        // Check budget warning first
        if (budget > 0 && product != null && viewModel.wouldExceedBudget(product.price)) {
            budgetWarningProductName = productName
            budgetWarningProduct = product
            budgetWarningRemaining = viewModel.budgetRemaining
            budgetWarningItemPrice = product.price
            showBudgetWarning = true
            return
        }

        // Check quantity warning
        if (wizardActive && product != null && product.suggestedPerDay > 0) {
            val suggestedQty = viewModel.getSuggestedQuantity(product)
            val existingQty = items.filter { it.product?.id == product.id }.sumOf { it.quantity }
            if (existingQty >= suggestedQty) {
                quantityWarningName = productName
                quantityWarningRecommended = suggestedQty
                quantityWarningCurrent = existingQty
                quantityPendingProductName = productName
                quantityPendingProduct = product
                quantityPendingIsIncrement = false
                showQuantityWarning = true
                return
            }
        }

        // Check price warning
        if (product != null && isExpensiveForProductType(product.price, product.categoryId, inputText)) {
            pendingProductName  = productName
            pendingProduct      = product
            pendingAvgPrice     = getAveragePriceForProductType(product.categoryId, inputText)
            pendingCategoryName = getCategoryById(product.categoryId)?.displayName ?: product.categoryId
            showExpensiveDialog = true
        } else {
            viewModel.addItem(productName)
            inputText = ""
        }
    }
    if (showExpensiveDialog) {
        AlertDialog(
            onDismissRequest = { showExpensiveDialog = false },
            shape = RoundedCornerShape(20.dp),
            title = {
                Text("💰 This item is expensive!", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "⚠️ \"$pendingProductName\" is more expensive than similar products.",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Do you still want to add it?",
                        fontSize = 16.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addItem(pendingProductName)
                        inputText = ""
                        showExpensiveDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EasylungaGreen)
                ) { Text("✅ Yes, add it", fontSize = 15.sp) }
            },
            dismissButton = {
                OutlinedButton(onClick = { showExpensiveDialog = false }) {
                    Text("❌ No, go back", fontSize = 15.sp)
                }
            }
        )
    }

    // ── Quantity warning dialog ────────────────────────────────────────────
    if (showQuantityWarning) {
        AlertDialog(
            onDismissRequest = { showQuantityWarning = false },
            shape = RoundedCornerShape(20.dp),
            title = {
                Text("📦 That's a lot!", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "The recommended amount for $days ${if (days == 1) "day" else "days"} and $people ${if (people == 1) "person" else "people"} is $quantityWarningRecommended.",
                        fontSize = 16.sp
                    )
                    Text(
                        "You already have $quantityWarningCurrent of \"$quantityWarningName\". Do you still want to add more?",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (quantityPendingIsIncrement) {
                            viewModel.incrementQuantity(quantityPendingItemId)
                        } else {
                            val product = quantityPendingProduct
                            if (product != null && isExpensiveForProductType(product.price, product.categoryId, inputText)) {
                                pendingProductName = quantityPendingProductName
                                pendingProduct = product
                                pendingAvgPrice = getAveragePriceForProductType(product.categoryId, inputText)
                                pendingCategoryName = getCategoryById(product.categoryId)?.displayName ?: product.categoryId
                                showExpensiveDialog = true
                            } else {
                                quantityPendingProduct?.let { viewModel.addItemWithProduct(it) }
                                inputText = ""
                            }
                        }
                        showQuantityWarning = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EasylungaGreen)
                ) { Text("✅ Yes, add more", fontSize = 15.sp) }
            },
            dismissButton = {
                OutlinedButton(onClick = { showQuantityWarning = false }) {
                    Text("❌ No, that's enough", fontSize = 15.sp)
                }
            }
        )
    }

    // ── Budget warning dialog ─────────────────────────────────────────────
    if (showBudgetWarning) {
        AlertDialog(
            onDismissRequest = { showBudgetWarning = false },
            shape = RoundedCornerShape(20.dp),
            title = {
                Text("💸 This exceeds your budget!", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "\"$budgetWarningProductName\" costs ${BudgetCalculator.formatEuro(budgetWarningItemPrice)} but you only have ${BudgetCalculator.formatEuro(budgetWarningRemaining)} left.",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Do you still want to add it?",
                        fontSize = 16.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showBudgetWarning = false
                        val product = budgetWarningProduct
                        // Continue with quantity and price checks
                        if (wizardActive && product != null && product.suggestedPerDay > 0) {
                            val suggestedQty = viewModel.getSuggestedQuantity(product)
                            val existingQty = items.filter { it.product?.id == product.id }.sumOf { it.quantity }
                            if (existingQty >= suggestedQty) {
                                quantityWarningName = budgetWarningProductName
                                quantityWarningRecommended = suggestedQty
                                quantityWarningCurrent = existingQty
                                quantityPendingProductName = budgetWarningProductName
                                quantityPendingProduct = product
                                quantityPendingIsIncrement = false
                                showQuantityWarning = true
                                return@Button
                            }
                        }
                        if (product != null && isExpensiveForProductType(product.price, product.categoryId, inputText)) {
                            pendingProductName = budgetWarningProductName
                            pendingProduct = product
                            pendingAvgPrice = getAveragePriceForProductType(product.categoryId, inputText)
                            pendingCategoryName = getCategoryById(product.categoryId)?.displayName ?: product.categoryId
                            showExpensiveDialog = true
                        } else {
                            viewModel.addItem(budgetWarningProductName)
                            inputText = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) { Text("✅ Yes, add anyway", fontSize = 15.sp) }
            },
            dismissButton = {
                OutlinedButton(onClick = { showBudgetWarning = false }) {
                    Text("❌ No, go back", fontSize = 15.sp)
                }
            }
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = DangerRed,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            if (isCaregiverMode) "Help with the list" else "My Shopping List",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        if (isCaregiverMode) {
                            Text(
                                "Caregiver mode",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        } else if (totalCount > 0) {
                            Text(
                                "🎯 $checkedCount / $totalCount items found",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = baseThemeColor,
                    titleContentColor = Color.White
                ),
                actions = {
                    if (items.isNotEmpty() && !isCaregiverMode) {
                        TextButton(onClick = { viewModel.clearAll() }) {
                            Text("Clear all", color = Color.White, fontSize = 15.sp)
                        }
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Budget bar — always reactive
                if (budget > 0) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "${BudgetCalculator.formatEuro(totalCost)} / ${BudgetCalculator.formatEuro(budget)}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = progressColor
                            )
                            Text(
                                when (budgetStatus) {
                                    BudgetStatus.OVER    -> "🔴 Over budget!"
                                    BudgetStatus.WARNING -> "🟡 Almost at limit"
                                    BudgetStatus.NOT_SET -> ""
                                    else                 -> "🟢 ${BudgetCalculator.formatEuro(viewModel.budgetRemaining)} left"
                                },
                                fontSize = 13.sp,
                                color = progressColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        LinearProgressIndicator(
                            progress = { viewModel.budgetProgress },
                            modifier = Modifier.fillMaxWidth().height(12.dp),
                            color = progressColor,
                            trackColor = Color(0xFFE0E0E0)
                        )
                    }
                }

                // Product preview cards
                if (searchResults.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        searchResults.take(3).forEach { product ->
                            val imageUrl = product.image
                            var showSearchDetailDialog by remember { mutableStateOf(false) }

                            // ── Detail dialog (igual que ShoppingItemRow) ──────────────────
                            if (showSearchDetailDialog) {
                                val color = sectionColor(getCategoryById(product.categoryId)?.section)
                                Dialog(
                                    onDismissRequest = { showSearchDetailDialog = false },
                                    properties = DialogProperties(usePlatformDefaultWidth = false)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.85f))
                                            .clickable { showSearchDetailDialog = false }
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(16.dp),
                                            modifier = Modifier
                                                .fillMaxWidth(0.88f)
                                                .clip(RoundedCornerShape(24.dp))
                                                .background(Color.White)
                                                .padding(24.dp)
                                                .clickable(enabled = false) {}
                                        ) {
                                            if (!imageUrl.isNullOrBlank()) {
                                                AsyncImage(
                                                    model = imageUrl,
                                                    contentDescription = product.name,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .aspectRatio(1f)
                                                        .clip(RoundedCornerShape(16.dp))
                                                        .background(Color(0xFFF5F5F5))
                                                )
                                            } else {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .aspectRatio(1f)
                                                        .clip(RoundedCornerShape(16.dp))
                                                        .background(color.copy(alpha = 0.10f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text("🛒", fontSize = 64.sp)
                                                }
                                            }

                                            Text(
                                                text = product.name,
                                                fontSize = 22.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = color,
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )

                                            val category = getCategoryById(product.categoryId)
                                            if (category != null) {
                                                Text(
                                                    text = "Aisle ${category.corsia} · ${category.displayName}",
                                                    fontSize = 15.sp,
                                                    color = Color.Gray
                                                )
                                            }

                                            if (product.price > 0) {
                                                Text(
                                                    text = "${BudgetCalculator.formatEuro(product.price)} per unit",
                                                    fontSize = 16.sp,
                                                    color = Color.Gray
                                                )
                                            }

                                            // Botó Afegir (acció principal des del dialog)
                                            Button(
                                                onClick = {
                                                    showSearchDetailDialog = false
                                                    tryAddWithCheck(product.name, product)
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = color),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.fillMaxWidth().height(52.dp)
                                            ) {
                                                Icon(Icons.Default.Add, contentDescription = null)
                                                Spacer(Modifier.width(8.dp))
                                                Text("Add to list", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                                            }

                                            OutlinedButton(
                                                onClick = { showSearchDetailDialog = false },
                                                shape = RoundedCornerShape(12.dp),
                                                border = androidx.compose.foundation.BorderStroke(1.5.dp, color.copy(alpha = 0.3f)),
                                                modifier = Modifier.fillMaxWidth().height(52.dp)
                                            ) {
                                                Text("Close", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                                            }
                                        }
                                    }
                                }
                            }

                            // ── Card de recomanació ────────────────────────────────────────
                            val rowBackground  = Color(0xFFF8FDF9)
                            val rowBorderColor = Color(0xFFCCCCCC)

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(rowBackground)
                                    .border(width = 1.dp, color = rowBorderColor, shape = RoundedCornerShape(14.dp))
                                    .combinedClickable(
                                        onClick = { tryAddWithCheck(product.name, product) },
                                        onLongClick = { showSearchDetailDialog = true },
                                        onLongClickLabel = "View product details"
                                    )
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                if (!imageUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = imageUrl,
                                        contentDescription = product.name,
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.White)
                                            .clickable { showSearchDetailDialog = true }
                                    )
                                    Spacer(Modifier.width(12.dp))
                                }

                                val shortName = product.name
                                    .split(" ").take(2).joinToString(" ")
                                    .replaceFirstChar { it.uppercase() }

                                Text(
                                    text = shortName,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )

                                IconButton(
                                    onClick = { showSearchDetailDialog = true },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = "Details of ${product.name}",
                                        tint = Color(0xFFAAAAAA),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { tryAddWithCheck(product.name, product) },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Add ${product.name}",
                                        tint = EasylungaGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Input row (visible in both user + caregiver modes)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                if (isCaregiverMode) "Add an item…" else "Type a product…",
                                fontSize = 17.sp
                            )
                        },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                // ERROR ARREGLADO AQUÍ: se evalúa si inputText no está vacío antes de proceder
                                if (inputText.isNotBlank()) {
                                    val match = searchResults.firstOrNull()
                                    tryAddWithCheck(match?.name ?: inputText, match)
                                    inputText = ""
                                }
                            }
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                if (!isCaregiverMode) {
                    // Action buttons (user mode)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = onAddWithWizard,
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("🧙 Wizard", fontSize = 15.sp)
                        }
                        OutlinedButton(
                            onClick = onReview,
                            modifier = Modifier.weight(1f).height(52.dp),
                            enabled = items.isNotEmpty(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("👤 Review", fontSize = 15.sp)
                        }
                    }

                    Button(
                        onClick = onStartNavigation,
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        enabled = items.any { !it.checked },
                        colors = ButtonDefaults.buttonColors(containerColor = EasylungaGreen),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(26.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Start Shopping", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Caregiver mode: lightweight hint + Done button
                    Text(
                        "You can add, remove, or edit items. When you're done, press Done.",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    Button(
                        onClick = { onCaregiverDone?.invoke() },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        enabled = onCaregiverDone != null,
                        colors = ButtonDefaults.buttonColors(containerColor = baseThemeColor),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Done", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    ) { paddingValues ->
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("🛒", fontSize = 72.sp)
                    Spacer(Modifier.height(4.dp))
                    OutlinedButton(
                        onClick = onAddWithWizard,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("🧙 Use the Wizard for suggestions")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    val suggestedQty = item.product?.let { viewModel.getSuggestedQuantity(it) }
                    val changeType = diff?.let { listDiff ->
                        listDiff.changes.find { change -> change.item.id == item.id }?.let { foundChange ->
                            when (foundChange.changeType) {
                                ItemChangeType.ADDED            -> "added"
                                ItemChangeType.REMOVED           -> "removed"
                                ItemChangeType.QUANTITY_CHANGED  -> "modified"
                                else                            -> null
                            }
                        }
                    }
                    // ──────────────────────────────────────────────────────────────────────────

                    ShoppingItemRow(
                        item = item,
                        budget = budget,
                        totalCost = totalCost,
                        suggestedQty = suggestedQty,
                        wizardActive = wizardActive,
                        onToggle = { viewModel.toggleChecked(item.id) },
                        onRemove = { viewModel.removeItem(item.id) },
                        onIncrement = {
                            val addCost = item.priceEuro
                            if (budget > 0 && viewModel.wouldExceedBudget(addCost)) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "🚫 Can't add more — only ${BudgetCalculator.formatEuro(viewModel.budgetRemaining)} left!"
                                    )
                                }
                            } else {
                                viewModel.incrementQuantity(item.id)
                            }
                        },
                        onDecrement = { viewModel.decrementQuantity(item.id) },
                        changeType = changeType
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ShoppingItemRow(
    item: ShoppingItem,
    budget: Double,
    totalCost: Double,
    suggestedQty: Int?,
    wizardActive: Boolean,
    onToggle: () -> Unit,
    onRemove: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    changeType: String? = null
) {
    val shortName = item.rawText
        .split(" ").take(2).joinToString(" ")
        .replaceFirstChar { it.uppercase() }

    val imageUrl = item.product?.image

    // Color único para todos — solo cambia si está marcado
    val rowBackground  = if (item.checked) Color(0xFFE8F5E9) else Color(0xFFF8FDF9)
    val rowBorderColor = if (item.checked) EasylungaGreen    else Color(0xFFCCCCCC)
    val rowBorderWidth = if (item.checked) 2.dp              else 1.dp
    val textColor      = if (item.checked) EasylungaGreen    else Color.Black
    val imageAlpha     = if (item.checked) 0.45f             else 1f

    var showDetailDialog by remember { mutableStateOf(false) }

    // ── Detail dialog ──────────────────────────────────────────────────────
    if (showDetailDialog) {
        val color = sectionColor(item.product?.let { getCategoryById(it.categoryId)?.section })
        Dialog(
            onDismissRequest = { showDetailDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f))
                    .clickable { showDetailDialog = false }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .padding(24.dp)
                        .clickable(enabled = false) {}
                ) {
                    if (!imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = item.rawText,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF5F5F5))
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(color.copy(alpha = 0.10f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🛒", fontSize = 64.sp)
                        }
                    }

                    Text(
                        text = item.rawText,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = color,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    item.product?.let { product ->
                        val category = getCategoryById(product.categoryId)
                        if (category != null) {
                            Text(
                                text = "Aisle ${category.corsia} · ${category.displayName}",
                                fontSize = 15.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    if (item.priceEuro > 0) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "${BudgetCalculator.formatEuro(item.priceEuro)} per unit",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                            if (item.quantity > 1) {
                                Text(
                                    text = "Total: ${BudgetCalculator.formatEuro(item.totalPrice)}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = color
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        OutlinedIconButton(
                            onClick = onDecrement,
                            modifier = Modifier.size(48.dp),
                            enabled = item.quantity > 1,
                            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp)
                        ) {
                            Text("−", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
                        }
                        Text(
                            text = "${item.quantity}",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = color,
                            modifier = Modifier.widthIn(min = 40.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        OutlinedIconButton(
                            onClick = onIncrement,
                            modifier = Modifier.size(48.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp)
                        ) {
                            Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
                        }
                    }

                    if (wizardActive && suggestedQty != null && suggestedQty != item.quantity) {
                        Text(
                            "💡 Suggested: $suggestedQty",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray
                        )
                    }

                    OutlinedButton(
                        onClick = { showDetailDialog = false },
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, color.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Text("Close", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    // ── Card en lista ──────────────────────────────────────────────────────
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(rowBackground)
            .border(width = rowBorderWidth, color = rowBorderColor, shape = RoundedCornerShape(14.dp))
            .combinedClickable(
                onClick = { },
                onLongClick = { showDetailDialog = true },
                onLongClickLabel = "View product details"
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        // Imagen
        if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Image of $shortName",
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .clickable { showDetailDialog = true },
                alpha = imageAlpha
            )
            Spacer(Modifier.width(12.dp))
        }

        // Nombre — solo una vez, sin subtítulo
        Text(
            text = shortName,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            textDecoration = if (item.checked) TextDecoration.LineThrough else null,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        // Controles de cantidad (solo si no está marcado)
        if (!item.checked) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                OutlinedIconButton(
                    onClick = onDecrement,
                    modifier = Modifier.size(30.dp),
                    enabled = item.quantity > 1,
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
                ) {
                    Text("−", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EasylungaGreen)
                }
                Text(
                    "${item.quantity}",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = EasylungaGreen,
                    modifier = Modifier.widthIn(min = 22.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                OutlinedIconButton(
                    onClick = onIncrement,
                    modifier = Modifier.size(30.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
                ) {
                    Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EasylungaGreen)
                }
            }
            Spacer(Modifier.width(4.dp))
        }

        // Info button
        IconButton(
            onClick = { showDetailDialog = true },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = "View details of $shortName",
                tint = if (item.checked) EasylungaGreen.copy(alpha = 0.5f) else Color(0xFFAAAAAA),
                modifier = Modifier.size(20.dp)
            )
        }

        // Remove button
        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(30.dp)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Remove $shortName",
                tint = Color.LightGray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}