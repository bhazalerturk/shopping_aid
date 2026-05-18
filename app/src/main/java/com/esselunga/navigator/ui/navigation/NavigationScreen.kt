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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.esselunga.navigator.util.RouteStep
import com.esselunga.navigator.viewmodel.ShoppingViewModel

// ── Palette ───────────────────────────────────────────────────────────────────
private val Green700           = Color(0xFF00843D)
private val Green50            = Color(0xFFF0FAF4)
private val Green100           = Color(0xFFE8F5E9)
private val Green200           = Color(0xFFC8E6D0)
private val Green600           = Color(0xFF4A7C5E)
private val Green800           = Color(0xFF1B4332)
private val CheckoutAmber      = Color.White
private val CheckoutAmberBorder= Green200
private val FinishBlue         = Color.White
private val FinishBlueBorder   = Green200

// ── Step metadata ─────────────────────────────────────────────────────────────
private data class StepMeta(
    val badge: String,
    val icon: ImageVector,
    val cardBackground: Color = Color.White,
    val cardBorder: Color     = Green200
)

private fun metaFor(step: RouteStep): StepMeta = when (step) {
    is RouteStep.EnterSection  -> StepMeta("Go to", Icons.Default.ShoppingCart)
    is RouteStep.PassThrough   -> StepMeta("Walk through", Icons.AutoMirrored.Filled.ArrowForward)
    is RouteStep.PickItem      -> StepMeta("Pick up", Icons.Default.ShoppingCart)
    is RouteStep.GoToCheckout  -> StepMeta("Head to checkout", Icons.Default.ShoppingCart, CheckoutAmber, CheckoutAmberBorder)
    is RouteStep.AskStaff      -> StepMeta("Ask a staff member", Icons.Default.Person)
    is RouteStep.Finish        -> StepMeta("All done!", Icons.Default.CheckCircle, FinishBlue, FinishBlueBorder)
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

    // Filter PickItem steps — they appear inline inside their EnterSection card
    val route = remember(itemsState) {
        viewModel.route.steps.filter { it !is RouteStep.PickItem }
    }
    val fullRoute = remember(itemsState) {
        viewModel.route.steps
    }

    var currentStepIndex by remember { mutableIntStateOf(0) }
    val currentStep = route.getOrNull(currentStepIndex)
    val totalSteps  = route.size

    // Items belonging to the current EnterSection
    val sectionItems = remember(currentStep, fullRoute) {
        if (currentStep !is RouteStep.EnterSection) return@remember emptyList()
        val idx = fullRoute.indexOfFirst {
            it is RouteStep.EnterSection && it.section == currentStep.section
        }
        if (idx < 0) return@remember emptyList()
        fullRoute.drop(idx + 1)
            .takeWhile { it is RouteStep.PickItem }
            .filterIsInstance<RouteStep.PickItem>()
    }

    val progressFraction by animateFloatAsState(
        targetValue = if (totalSteps > 0) (currentStepIndex + 1f) / totalSteps else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "progressAnim"
    )

    Scaffold(
        containerColor = Green50,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(48.dp)
                            .semantics { contentDescription = "Exit navigation" }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Green800,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Green50),
                actions = {
                    IconButton(
                        onClick = onHelp,
                        modifier = Modifier
                            .size(48.dp)
                            .semantics { contentDescription = "Help" }
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Green600, modifier = Modifier.size(24.dp))
                    }
                    IconButton(
                        onClick = onOpenMap,
                        modifier = Modifier
                            .size(48.dp)
                            .semantics { contentDescription = "View store map" }
                    ) {
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
            // ── Scrollable content ────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(Modifier.height(4.dp))

                ProgressHeader(
                    currentIndex = currentStepIndex,
                    total = totalSteps,
                    fraction = progressFraction
                )

                if (currentStep != null) {
                    StepCard(
                        step = currentStep,
                        sectionItems = sectionItems,
                        onToggleItem = { id -> viewModel.toggleChecked(id) }
                    )
                } else {
                    FinishedCard()
                }

                Spacer(Modifier.height(8.dp))
            }

            // ── Fixed bottom buttons ──────────────────────────────────────────
            NavigationButtons(
                currentIndex = currentStepIndex,
                totalSteps = totalSteps,
                onPrevious = { currentStepIndex-- },
                onNext = { if (currentStepIndex < totalSteps - 1) currentStepIndex++ },
                onFinish = onFinish
            )
        }
    }
}

// ── Progress header ────────────────────────────────────────────────────────────
@Composable
private fun ProgressHeader(
    currentIndex: Int,
    total: Int,
    fraction: Float
) {
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
    sectionItems: List<RouteStep.PickItem>,
    onToggleItem: (String) -> Unit
) {
    val meta = metaFor(step)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = meta.cardBackground),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, meta.cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            StepTypeBadge(label = meta.badge, icon = meta.icon)

            Spacer(Modifier.height(14.dp))

            val titleText = when (step) {
                is RouteStep.EnterSection -> step.section.label
                is RouteStep.PassThrough  -> step.section.label
                is RouteStep.PickItem     -> step.item.rawText
                is RouteStep.GoToCheckout -> "Checkout"
                is RouteStep.AskStaff     -> "A staff member"
                is RouteStep.Finish       -> "You're all done!"
            }

            Text(
                text = titleText,
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = Green800,
                lineHeight = 44.sp
            )

            // AskStaff: list items below the title
            if (step is RouteStep.AskStaff && step.items.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                step.items.forEach { item ->
                    Text(
                        text = "· ${item.rawText}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Green800,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }

            // EnterSection: checklist below the title
            if (sectionItems.isNotEmpty()) {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Pick up these items:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Green600
                )
                Spacer(Modifier.height(10.dp))
                sectionItems.forEach { pickStep ->
                    ChecklistRow(
                        text      = pickStep.item.rawText,
                        quantity  = pickStep.item.quantity,
                        checked   = pickStep.item.checked,
                        imageUrl  = pickStep.item.product?.image,
                        onClick   = { onToggleItem(pickStep.item.id) }
                    )
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

// ── Finished card ─────────────────────────────────────────────────────────────
@Composable
private fun FinishedCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = FinishBlue),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, FinishBlueBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Green700,
                modifier = Modifier.size(52.dp)
            )
            Text(
                "All done!",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = Green800
            )
            Text(
                "You can head to the exit.",
                fontSize = 20.sp,
                color = Green600
            )
        }
    }
}

// ── Step type badge ────────────────────────────────────────────────────────────
@Composable
private fun StepTypeBadge(label: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .background(Green100, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Green700,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Green800
        )
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
    var showImageDialog by remember { mutableStateOf(false) }

    // Full-screen image dialog
    if (showImageDialog && !imageUrl.isNullOrBlank()) {
        Dialog(
            onDismissRequest = { showImageDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f))
                    .clickable { showImageDialog = false }
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = text,
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                )
                // Tap to close hint
                Text(
                    text = "Tap to close",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp)
                )
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (checked) Green100 else Color(0xFFF8FDF9))
            .border(
                width = 1.5.dp,
                color = if (checked) Green700 else Green200,
                shape = RoundedCornerShape(12.dp)
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = { if (!imageUrl.isNullOrBlank()) showImageDialog = true },
                onLongClickLabel = "View product image"
            )
            .semantics { role = Role.Checkbox }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { onClick() },
            modifier = Modifier
                .size(32.dp)
                .semantics {
                    contentDescription = if (checked) "$text — checked" else "$text — unchecked"
                },
            colors = CheckboxDefaults.colors(
                checkedColor   = Green700,
                uncheckedColor = Green600,
                checkmarkColor = Color.White
            )
        )

        Spacer(Modifier.width(12.dp))

        // Product image thumbnail
        if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White),
                alpha = if (checked) 0.4f else 1f
            )
            Spacer(Modifier.width(12.dp))
        }

        Text(
            text = if (quantity > 1) "$text  ×$quantity" else text,
            fontSize = 19.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (checked) Green600 else Green800,
            textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None,
            modifier = Modifier.weight(1f)
        )

        if (quantity > 1) {
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .background(Green200, RoundedCornerShape(99.dp))
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "×$quantity",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Green800
                )
            }
        }
    }
}

// ── Navigation buttons ────────────────────────────────────────────────────────
@Composable
private fun NavigationButtons(
    currentIndex: Int,
    totalSteps: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onFinish: () -> Unit
) {
    val isLast = currentIndex == totalSteps - 1

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (currentIndex > 0) {
            OutlinedButton(
                onClick = onPrevious,
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
                    .semantics { contentDescription = "Previous step" },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Green800),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Green200)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Back", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Button(
            onClick = if (isLast) onFinish else onNext,
            modifier = Modifier
                .weight(1f)
                .height(64.dp)
                .semantics {
                    contentDescription = if (isLast) "Finish shopping" else "Go to next step"
                },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Green700)
        ) {
            Text(
                text = if (isLast) "Done" else "Next",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = if (isLast) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}