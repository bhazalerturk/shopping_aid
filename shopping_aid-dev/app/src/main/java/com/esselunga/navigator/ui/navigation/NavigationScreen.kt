package com.esselunga.navigator.ui.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.esselunga.navigator.data.StoreSection
import com.esselunga.navigator.util.RouteStep
import com.esselunga.navigator.util.WalkDirection
import com.esselunga.navigator.util.computeDirection
import com.esselunga.navigator.util.indexOfSectionInPath
import com.esselunga.navigator.util.sectionAisle
import com.esselunga.navigator.viewmodel.ShoppingViewModel
import androidx.compose.ui.draw.rotate

// ── Palette ───────────────────────────────────────────────────────────────────
private val Green700 = Color(0xFF00843D)
private val Green50  = Color(0xFFF0FAF4)
private val Green100 = Color(0xFFE8F5E9)
private val Green200 = Color(0xFFC8E6D0)
private val Green600 = Color(0xFF4A7C5E)
private val Green800 = Color(0xFF1B4332)

// ── Section display metadata ──────────────────────────────────────────────────
private data class SectionDisplay(val label: String, val emoji: String)

private fun displayFor(section: StoreSection): SectionDisplay = when (section) {
    StoreSection.FRESHPRODUCTS -> SectionDisplay("Fresh products",  "🥦")
    StoreSection.BAKERY        -> SectionDisplay("Bakery",          "🥖")
    StoreSection.DAIRY         -> SectionDisplay("Dairy",           "🥛")
    StoreSection.DELI          -> SectionDisplay("Deli counter",    "🍖")
    StoreSection.MEAT          -> SectionDisplay("Meat",            "🥩")
    StoreSection.FROZEN        -> SectionDisplay("Frozen foods",    "🧊")
    StoreSection.BREAKFAST     -> SectionDisplay("Breakfast",       "🥣")
    StoreSection.PASTA_RICE    -> SectionDisplay("Pasta & rice",    "🍝")
    StoreSection.DISPENSA      -> SectionDisplay("Pantry",          "🫙")
    StoreSection.PERSONAL_CARE -> SectionDisplay("Personal care",   "🧴")
    StoreSection.CLEANING      -> SectionDisplay("Cleaning",        "🧹")
    StoreSection.PET           -> SectionDisplay("Pet supplies",    "🐾")
    StoreSection.DRINKS        -> SectionDisplay("Drinks",          "🧃")
}

// ── Step action pill metadata ─────────────────────────────────────────────────
private data class StepMeta(
    val actionEmoji: String,
    val actionLabel: String,
    val icon: ImageVector,
    val cardBackground: Color = Color.White,
    val cardBorder: Color     = Green200
)

private fun metaFor(step: RouteStep): StepMeta = when (step) {
    is RouteStep.EnterSection  -> StepMeta("🚶", "Get to section",     Icons.AutoMirrored.Filled.ArrowForward)
    is RouteStep.PassThrough   -> StepMeta("🚶", "Walk through",       Icons.AutoMirrored.Filled.ArrowForward)
    is RouteStep.PickSection   -> StepMeta("🛒", "Pick up items here", Icons.Default.ShoppingCart)
    is RouteStep.PickItem      -> StepMeta("🛒", "Pick up items here", Icons.Default.ShoppingCart)
    is RouteStep.GoToCheckout  -> StepMeta("💳", "Head to checkout",   Icons.Default.ShoppingCart, Color.White, Green200)
    is RouteStep.AskStaff      -> StepMeta("🙋", "Ask a staff member", Icons.Default.Person)
    is RouteStep.Finish        -> StepMeta("🎉", "End of the route",   Icons.Default.CheckCircle,  Color.White, Green200)
}

// ── Screen ────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationScreen(
    viewModel: ShoppingViewModel,
    onBack: () -> Unit,
    onOpenMap: () -> Unit,
    onHelp: () -> Unit,
    onFinish: () -> Unit
) {
    val itemsState by viewModel.items.collectAsState()

    // Construim la route expandida: EnterSection → PickSection (si té items)
    val route: List<RouteStep> = remember(itemsState) {
        val fullSteps = viewModel.route.steps
        buildList {
            var i = 0
            while (i < fullSteps.size) {
                val step = fullSteps[i]
                when (step) {
                    is RouteStep.EnterSection -> {
                        add(step)
                        // Recull tots els PickItem consecutius
                        val pickItems = mutableListOf<RouteStep.PickItem>()
                        var j = i + 1
                        while (j < fullSteps.size && fullSteps[j] is RouteStep.PickItem) {
                            pickItems.add(fullSteps[j] as RouteStep.PickItem)
                            j++
                        }
                        if (pickItems.isNotEmpty()) {
                            add(RouteStep.PickSection(step.section, pickItems))
                        }
                        i = j
                    }
                    is RouteStep.PickItem -> i++ // ja consumit dins EnterSection
                    else -> { add(step); i++ }
                }
            }
        }
    }

    val routePath = remember(itemsState) { viewModel.route.path }
    var showUncheckedReminder by remember { mutableStateOf(false) }
    var uncheckedItemsList by remember { mutableStateOf<List<String>>(emptyList()) }
    var showWeighReminder by remember { mutableStateOf(false) }
    var currentStepIndex by remember {
        mutableIntStateOf(viewModel.currentNavigationStep)
    }
    val currentStep = route.getOrNull(currentStepIndex)
    val totalSteps  = route.size
    val isLastStep  = currentStepIndex == totalSteps - 1

    LaunchedEffect(currentStepIndex) {
        viewModel.setNavigationStep(currentStepIndex)
    }


    fun nodeToFriendlyLabel(nodeId: String): String? = when {
        nodeId.startsWith("FRESHPRODUCTS") -> "Fresh products"
        nodeId == "DELI"          -> "Deli counter"
        nodeId == "MEAT"          -> "Meat"
        nodeId == "BAKERY"        -> "Bakery"
        nodeId == "DAIRY"         -> "Dairy (aisle 1)"
        nodeId == "DISPENSA"      -> "Pantry (aisle 2)"
        nodeId == "PASTA_RICE"    -> "Pasta & rice (aisle 3)"
        nodeId == "BREAKFAST"     -> "Breakfast (aisle 4)"
        nodeId == "PERSONAL_CARE" -> "Personal care (aisle 5)"
        nodeId == "CLEANING"      -> "Cleaning (aisle 6)"
        nodeId == "PET"           -> "Pet supplies (aisle 7)"
        nodeId == "FROZEN"        -> "Frozen (aisle 10)"
        nodeId == "DRINKS"        -> "Drinks (aisle 11)"
        nodeId == "CHECKOUT1"     -> "Checkout"
        nodeId == "CHECKOUT2"     -> "Checkout"
        else -> null
    }

    val directionInfo: Triple<WalkDirection, Int?, List<String>>? = remember(currentStepIndex, routePath) {
        val section = when (currentStep) {
            is RouteStep.EnterSection -> currentStep.section
            is RouteStep.PassThrough  -> currentStep.section
            else -> null
        } ?: return@remember null

        val nodeIdx = indexOfSectionInPath(routePath, section)
        if (nodeIdx < 0) return@remember null

        // Busca el pas anterior que sigui EnterSection o PassThrough
        val fromIdx = (currentStepIndex - 1 downTo 0)
            .mapNotNull { i ->
                when (val prev = route.getOrNull(i)) {
                    is RouteStep.EnterSection -> indexOfSectionInPath(routePath, prev.section)
                    is RouteStep.PassThrough  -> indexOfSectionInPath(routePath, prev.section)
                    else -> null
                }
            }
            .firstOrNull { it >= 0 } ?: 0

        val intermediateNodes = routePath
            .subList(fromIdx.coerceAtLeast(0), nodeIdx.coerceAtMost(routePath.size))
            .filter { !it.startsWith("WALK_") }
            .mapNotNull { nodeToFriendlyLabel(it) }
            .distinct()

        Triple(
            computeDirection(routePath, nodeIdx),
            sectionAisle[section],
            intermediateNodes
        )
    }

    // Progress
    val progressFraction by animateFloatAsState(
        targetValue = if (totalSteps > 0) (currentStepIndex + 1).toFloat() / totalSteps else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "progressAnim"
    )

    fun handleNext() {
        val callIsLast = currentStepIndex == totalSteps - 1
        val currStep = route.getOrNull(currentStepIndex)

        // Check for unchecked items in PickSection
        if (currStep is RouteStep.PickSection) {
            // Get fresh checked state from current items state
            val freshItems = currStep.items.mapNotNull { pickItem ->
                itemsState.find { it.id == pickItem.item.id }
            }
            val uncheckedItems = freshItems.filter { !it.checked }
            if (uncheckedItems.isNotEmpty()) {
                uncheckedItemsList = uncheckedItems.map { it.rawText }
                showUncheckedReminder = true
                return
            }
        }

        val isFreshProducts = currStep is RouteStep.PickSection &&
                currStep.section == StoreSection.FRESHPRODUCTS

        if (isFreshProducts) {
            showWeighReminder = true
            return
        }

        if (callIsLast) {
            viewModel.setNavigationStep(0)
            onFinish()
        } else currentStepIndex++
    }


    fun handlePrev() {
        if (currentStepIndex > 0) currentStepIndex--
    }

    if (showWeighReminder) {
        AlertDialog(
            onDismissRequest = {
                showWeighReminder = false
                currentStepIndex++
            },
            shape = RoundedCornerShape(20.dp),
            title = {
                Text("⚖️ Don't forget to weigh!", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Green800)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("🥦🍎🥕", fontSize = 48.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    Text(
                        "Please remember to weigh your fresh fruits and vegetables at the weighing station before moving on!",
                        fontSize = 16.sp,
                        color = Green800
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showWeighReminder = false
                        currentStepIndex++
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Green700)
                ) {
                    Text("✅ Got it!", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showUncheckedReminder) {
        AlertDialog(
            onDismissRequest = { showUncheckedReminder = false },
            shape = RoundedCornerShape(20.dp),
            title = {
                Text("⚠️ Wait! Some items aren't checked", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Green800)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "You might be forgetting these items:",
                        fontSize = 16.sp,
                        color = Green800
                    )
                    uncheckedItemsList.forEach { itemName ->
                        Text(
                            "• $itemName",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Green700
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Do you want to continue anyway?",
                        fontSize = 15.sp,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showUncheckedReminder = false
                        // Check if it's also fresh products to chain the weigh dialog
                        val currStep = route.getOrNull(currentStepIndex)
                        if (currStep is RouteStep.PickSection && currStep.section == StoreSection.FRESHPRODUCTS) {
                            showWeighReminder = true
                        } else if (currentStepIndex == totalSteps - 1) {
                            viewModel.setNavigationStep(0)
                            onFinish()
                        } else {
                            currentStepIndex++
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Green700)
                ) {
                    Text("Yes, continue", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showUncheckedReminder = false }) {
                    Text("No, go back", fontSize = 15.sp, color = Green800)
                }
            }
        )
    }

    Scaffold(
        containerColor = Green50,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.setNavigationStep(0)
                            onBack()
                        },
                        modifier = Modifier.size(48.dp).semantics { contentDescription = "Exit navigation" }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Green800, modifier = Modifier.size(26.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Green50),
                actions = {
                    IconButton(onClick = onHelp, modifier = Modifier.size(48.dp).semantics { contentDescription = "Help" }) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Green600, modifier = Modifier.size(24.dp))
                    }
                    IconButton(onClick = onOpenMap, modifier = Modifier.size(48.dp).semantics { contentDescription = "View store map" }) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Green600, modifier = Modifier.size(24.dp))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(Modifier.height(4.dp))

                ProgressHeader(
                    currentIndex = currentStepIndex,
                    total        = totalSteps,
                    fraction     = progressFraction
                )

                if (currentStep != null) {
                    StepCard(
                        step         = currentStep,
                        directionInfo = directionInfo,
                        onToggleItem  = { id -> viewModel.toggleChecked(id) }
                    )
                } else {
                    FinishedCard()
                }

                Spacer(Modifier.height(8.dp))
            }

            NavigationButtons(
                hasPrev    = currentStepIndex > 0,
                isLast     = isLastStep,
                onPrevious = ::handlePrev,
                onNext     = ::handleNext
            )
        }
    }
}

// ── Progress header ────────────────────────────────────────────────────────────
@Composable
private fun ProgressHeader(currentIndex: Int, total: Int, fraction: Float) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Step ${currentIndex + 1} of $total",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = Green600,
            modifier = Modifier.semantics {
                contentDescription = "Progress: step ${currentIndex + 1} of $total"
            }
        )
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(99.dp)),
            color = Green700,
            trackColor = Green200,
            strokeCap = StrokeCap.Round
        )
    }
}

// ── Step card ──────────────────────────────────────────────────────────────────
@Composable
private fun StepCard(
    step: RouteStep,
    directionInfo: Triple<WalkDirection, Int?, List<String>>?,
    onToggleItem: (String) -> Unit
) {
    val meta = metaFor(step)

    val sectionDisplay: SectionDisplay? = when (step) {
        is RouteStep.EnterSection -> displayFor(step.section)
        is RouteStep.PassThrough  -> displayFor(step.section)
        is RouteStep.PickSection  -> displayFor(step.section)
        else                      -> null
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = meta.cardBackground),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, meta.cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val titleText = when (step) {
                is RouteStep.EnterSection -> sectionDisplay?.label ?: step.section.name
                is RouteStep.PassThrough  -> sectionDisplay?.label ?: step.section.name
                is RouteStep.PickSection  -> sectionDisplay?.label ?: step.section.name
                is RouteStep.PickItem     -> step.item.rawText
                is RouteStep.GoToCheckout -> "Checkout"
                is RouteStep.AskStaff     -> "Ask a staff member"
                is RouteStep.Finish       -> "You're all done!"
            }

            // Direction banner — solo en EnterSection y PassThrough
            if (directionInfo != null && step !is RouteStep.PickSection) {
                Spacer(Modifier.height(20.dp))
                DirectionBanner(
                    direction = directionInfo.first,
                    aisle     = directionInfo.second,
                    waypoints = directionInfo.third
                )
                Spacer(Modifier.height(24.dp))
            }

            // Action pill
            ActionPill(emoji = meta.actionEmoji, label = meta.actionLabel)
            Spacer(Modifier.height(20.dp))

            // Big emoji
            val bigEmoji = sectionDisplay?.emoji ?: when (step) {
                is RouteStep.GoToCheckout -> "💳"
                is RouteStep.AskStaff     -> "🙋"
                is RouteStep.Finish       -> "✅"
                else                      -> ""
            }
            if (bigEmoji.isNotEmpty()) {
                Text(text = bigEmoji, fontSize = 72.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(8.dp))
            }

            // Title
            Text(
                text = titleText,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Green800,
                lineHeight = 40.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // AskStaff list
            if (step is RouteStep.AskStaff && step.items.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                step.items.forEach { item ->
                    Text(
                        text = "· ${item.rawText}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Green800,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }

            // PickSection checklist
            if (step is RouteStep.PickSection) {
                Spacer(Modifier.height(24.dp))
                HorizontalDivider(color = Green200)
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Items to pick up:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Green600,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                step.items.forEach { pickStep ->
                    ChecklistRow(
                        text     = pickStep.item.rawText,
                        quantity = pickStep.item.quantity,
                        checked  = pickStep.item.checked,
                        imageUrl = pickStep.item.product?.image,
                        onClick  = { onToggleItem(pickStep.item.id) }
                    )
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

// ── Direction banner ───────────────────────────────────────────────────────────
@Composable
private fun DirectionBanner(
    direction: WalkDirection,
    aisle: Int?,
    waypoints: List<String> = emptyList()
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Green800)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (waypoints.isNotEmpty()) {
            Text(
                text = "After " + waypoints.joinToString(" → "),
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.85f),
                lineHeight = 20.sp
            )
            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                val isUp = !direction.label.contains("left", ignoreCase = true) &&
                        !direction.label.contains("right", ignoreCase = true)

                Icon(
                    imageVector = if (direction.label.contains("left", ignoreCase = true))
                        Icons.AutoMirrored.Filled.ArrowBack
                    else
                        Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = direction.label,
                    tint = Color.White,
                    modifier = Modifier
                        .size(48.dp)
                        .rotate(if (isUp) -90f else 0f)
                        .semantics { contentDescription = direction.label }
                )
                Text(
                    text = direction.label,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 30.sp
                )
            }
            if (aisle != null) {
                Spacer(Modifier.width(12.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Green700)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text("Aisle", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Green200)
                    Text("$aisle", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

// ── Action pill ────────────────────────────────────────────────────────────────
@Composable
private fun ActionPill(emoji: String, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(Green100, RoundedCornerShape(99.dp))
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(text = emoji, fontSize = 22.sp)
        Spacer(Modifier.width(8.dp))
        Text(text = label, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Green800)
    }
}

// ── Finished card ─────────────────────────────────────────────────────────────
@Composable
private fun FinishedCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Green200),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "✅", fontSize = 72.sp)
            Text("All done!", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = Green800)
            Text("You can head to the exit.", fontSize = 18.sp, color = Green600, textAlign = TextAlign.Center)
        }
    }
}

// ── Checklist row ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ChecklistRow(
    text: String,
    quantity: Int,
    checked: Boolean,
    imageUrl: String? = null,
    onClick: () -> Unit
) {
    var showDetailDialog by remember { mutableStateOf(false) }

    if (showDetailDialog) {
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
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .padding(24.dp)
                ) {
                    if (!imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = text,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF5F5F5))
                        )
                    }
                    Text(text = text, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Green800, lineHeight = 30.sp, textAlign = TextAlign.Center)
                    if (quantity > 1) {
                        Text("Quantity: $quantity", fontSize = 18.sp, color = Green600)
                    }
                    OutlinedButton(
                        onClick = { showDetailDialog = false },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Green800),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Green200),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Text("Close", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    val shortName      = text.split(" ").take(2).joinToString(" ").replaceFirstChar { it.uppercase() }
    val rowBackground  = if (checked) Green700    else Color(0xFFF8FDF9)
    val rowBorderColor = if (checked) Green800    else Green200
    val rowBorderWidth = if (checked) 2.5.dp      else 1.5.dp
    val textColor      = if (checked) Color.White else Green800
    val imageAlpha     = if (checked) 0.45f       else 1f

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(rowBackground)
            .border(width = rowBorderWidth, color = rowBorderColor, shape = RoundedCornerShape(14.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = { showDetailDialog = true },
                onLongClickLabel = "View product details"
            )
            .semantics { role = Role.Checkbox }
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { onClick() },
            modifier = Modifier.size(36.dp).semantics {
                contentDescription = if (checked) "$text — checked" else "$text — not checked"
            },
            colors = CheckboxDefaults.colors(
                checkedColor   = Color.White,
                uncheckedColor = Green600,
                checkmarkColor = Green700
            )
        )
        Spacer(Modifier.width(12.dp))
        if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "View image of $shortName",
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .clickable { showDetailDialog = true },
                alpha = imageAlpha
            )
            Spacer(Modifier.width(12.dp))
        }
        Text(
            text = if (quantity > 1) "$shortName ×$quantity" else shortName,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        IconButton(onClick = { showDetailDialog = true }, modifier = Modifier.size(36.dp)) {
            Icon(
                Icons.Default.Info,
                contentDescription = "View details of $shortName",
                tint = if (checked) Color.White.copy(alpha = 0.7f) else Green600,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ── Navigation buttons ────────────────────────────────────────────────────────
@Composable
private fun NavigationButtons(
    hasPrev: Boolean,
    isLast: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (hasPrev) {
            OutlinedButton(
                onClick = onPrevious,
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
                    .semantics { contentDescription = "Go back to previous step" },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Green800),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Green200)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                Text("Back", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
        Button(
            onClick = onNext,
            modifier = Modifier
                .weight(if (hasPrev) 1f else 2f)
                .height(64.dp)
                .semantics { contentDescription = if (isLast) "Finish shopping" else "Go to next step" },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Green700)
        ) {
            Text(
                text = if (isLast) "Done ✓" else "Next",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            if (!isLast) {
                Spacer(Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(22.dp))
            }
        }
    }
}