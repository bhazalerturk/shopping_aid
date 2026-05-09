package com.esselunga.navigator.ui.list

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import com.esselunga.navigator.data.Product
import com.esselunga.navigator.data.ShoppingItem
import com.esselunga.navigator.data.StoreSection
import com.esselunga.navigator.util.BudgetCalculator
import com.esselunga.navigator.util.BudgetStatus
import com.esselunga.navigator.data.searchProducts
import com.esselunga.navigator.data.getCategoryById
import com.esselunga.navigator.viewmodel.ShoppingViewModel
import kotlinx.coroutines.launch

private val EasylungaGreen = Color(0xFF00843D)
private val WarningYellow = Color(0xFFF9A825)
private val DangerRed = Color(0xFFD32F2F)

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

private fun sectionEmoji(section: StoreSection?): String = when (section) {
    StoreSection.FRESHPRODUCTS      -> "🥦"
    StoreSection.BAKERY       -> "🍞"
    StoreSection.PASTA_RICE   -> "🍝"
    StoreSection.DISPENSA   -> "🫙"
    StoreSection.DAIRY        -> "🥛"
    StoreSection.DELI         -> "🥩"
    StoreSection.MEAT         -> "🍗"
    StoreSection.FROZEN       -> "🧊"
    StoreSection.BREAKFAST    -> "🥣"
    StoreSection.DRINKS       -> "🥤"
    StoreSection.PERSONAL_CARE-> "🧴"
    StoreSection.CLEANING     -> "🧹"
    StoreSection.PET          -> "🐾"
    null                      -> "❓"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    viewModel: ShoppingViewModel,
    onStartNavigation: () -> Unit,
    onReview: () -> Unit,
    onAddWithWizard: () -> Unit
) {
    val items by viewModel.items.collectAsState()
    val budget by viewModel.budget.collectAsState()
    val totalCost by viewModel.totalCostFlow.collectAsState()
    val days by viewModel.wizardDays.collectAsState()
    val people by viewModel.wizardPeople.collectAsState()
    val wizardActive = days > 1 || people > 1

    var inputText by remember { mutableStateOf("") }
    val searchResults = remember(inputText) {
        if (inputText.length >= 2) {
            searchProducts(inputText)
        } else {
            emptyList()
        }
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Live preview of recognized product as user types
    /*
    val previewCategory: Product? = remember(inputText) {
        if (inputText.length >= 2) findCategory(inputText) else null
    }
    val previewSuggestedQty: Int? = remember(previewCategory, days, people) {
        previewCategory?.let { viewModel.getSuggestedQuantity(it) }
    }

     */

    val budgetStatus = BudgetCalculator.budgetStatus(totalCost, budget)
    val progressColor by animateColorAsState(
        targetValue = when (budgetStatus) {
            BudgetStatus.OVER    -> DangerRed
            BudgetStatus.WARNING -> WarningYellow
            else                 -> EasylungaGreen
        },
        animationSpec = tween(400), label = "budgetColor"
    )

    val checkedCount = items.count { it.checked }
    val totalCount = items.size

    fun tryAddItem(text: String) {
        if (text.isBlank()) return

        viewModel.addItem(text)

        inputText = ""
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
                        Text("My Shopping List", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        if (totalCount > 0) {
                            Text(
                                "🎯 $checkedCount / $totalCount items found",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EasylungaGreen,
                    titleContentColor = Color.White
                ),
                actions = {
                    if (items.isNotEmpty()) {
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

                // Product preview card — shows when text matches a product
                if (searchResults.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column {
                            searchResults.forEach { product ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.addItem(product.name)
                                            inputText = ""
                                        }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(Modifier.weight(1f)) {
                                        Text(product.name, fontWeight = FontWeight.Bold)
                                        Text(text = getCategoryById(product.categoryId)?.displayName ?: product.categoryId,
                                            fontSize = 12.sp, color = Color.Gray)
                                    }
                                    Text("${product.price} €")
                                }
                            }
                        }
                    }
                }

                // Input row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type a product…", fontSize = 17.sp) },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                val match = searchResults.firstOrNull()

                                if (match != null) {
                                    viewModel.addItem(match.name)
                                } else {
                                    viewModel.addItem(inputText)
                                }

                                inputText = ""
                            }
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    IconButton(
                        onClick = {
                            val match = searchResults.firstOrNull()

                            if (match != null) {
                                viewModel.addItem(match.name)
                            } else {
                                viewModel.addItem(inputText)
                            }

                            inputText = ""
                        },
                        modifier = Modifier.size(56.dp),
                        colors = IconButtonDefaults.iconButtonColors(containerColor = EasylungaGreen)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Action buttons
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
            }
        }
    ) { paddingValues ->
        if (items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("🛒", fontSize = 72.sp)
                    Text("Your list is empty", color = Color.DarkGray, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Type a product above to get started", color = Color.LightGray, fontSize = 15.sp)
                    if (!wizardActive) {
                        Spacer(Modifier.height(4.dp))
                        OutlinedButton(onClick = onAddWithWizard, shape = RoundedCornerShape(12.dp)) {
                            Text("🧙 Use the Wizard for suggestions")
                        }
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
                        onDecrement = { viewModel.decrementQuantity(item.id) }
                    )
                }
            }
        }
    }
}

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
    onDecrement: () -> Unit
) {
    val color = sectionColor(null)
    val priceColor = when {
        item.checked                                      -> Color.Gray
        budget > 0 && totalCost > budget                  -> DangerRed
        else                                              -> color
    }
    val bgColor = when {
        item.checked        -> Color(0xFFF5F5F5)
        item.product == null -> Color(0xFFFFF3E0)
        else                -> color.copy(alpha = 0.06f)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        // Colored left stripe
        Row(Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(IntrinsicSize.Min)
                    .background(
                        if (item.checked) Color.LightGray else color,
                        RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp)
                    )
                    .defaultMinSize(minHeight = 72.dp)
            )
            Column(Modifier.fillMaxWidth().padding(10.dp, 10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        sectionEmoji(null),
                        fontSize = 26.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Checkbox(
                        checked = item.checked,
                        onCheckedChange = { onToggle() },
                        colors = CheckboxDefaults.colors(checkedColor = color),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.rawText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            textDecoration = if (item.checked) TextDecoration.LineThrough else null,
                            color = if (item.checked) Color.Gray else Color.Black
                        )
                        if (item.product != null) {
                            Text(
                                text = "Aisle ${null} · ${null}",
                                fontSize = 12.sp,
                                color = color
                            )
                        } else {
                            Text(
                                "Not recognized — ask staff",
                                fontSize = 12.sp,
                                color = Color(0xFFF57C00)
                            )
                        }
                    }
                    if (item.priceEuro > 0) {
                        Text(
                            BudgetCalculator.formatEuro(item.totalPrice),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = priceColor
                        )
                    }
                    Spacer(Modifier.width(2.dp))
                    IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Remove",
                            tint = Color.LightGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Quantity row
                if (!item.checked) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 68.dp, top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedIconButton(
                            onClick = onDecrement,
                            modifier = Modifier.size(34.dp),
                            enabled = item.quantity > 1,
                            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp)
                        ) {
                            Text("−", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
                        }
                        Text(
                            "${item.quantity}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = color,
                            modifier = Modifier.widthIn(min = 28.dp)
                        )
                        OutlinedIconButton(
                            onClick = onIncrement,
                            modifier = Modifier.size(34.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp)
                        ) {
                            Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
                        }
                        if (item.priceEuro > 0 && item.quantity > 1) {
                            Text(
                                "${BudgetCalculator.formatEuro(item.priceEuro)} each",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        // Suggestion hint
                        if (wizardActive && suggestedQty != null && suggestedQty != item.quantity) {
                            Text(
                                "💡 Suggested: $suggestedQty",
                                fontSize = 12.sp,
                                color = EasylungaGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
