package com.esselunga.navigator.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esselunga.navigator.ui.theme.EasylungaGreen
import com.esselunga.navigator.ui.theme.EasylungaLightGreen

@Composable
fun HomeScreen(onStart: () -> Unit, onHelp: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(modifier = Modifier.fillMaxSize().background(EasylungaLightGreen)) {
        // Help/settings icon top-right
        IconButton(
            onClick = onHelp,
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Settings, contentDescription = "Help & Settings", tint = EasylungaGreen, modifier = Modifier.size(28.dp))
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Easylunga",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = EasylungaGreen
            )
            Text(
                text = "Easy Grocery Shopping",
                fontSize = 18.sp,
                color = EasylungaGreen.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(56.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(180.dp)
                    .scale(scale)
                    .border(3.dp, EasylungaGreen.copy(alpha = 0.3f), CircleShape)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(140.dp)
                        .border(3.dp, EasylungaGreen.copy(alpha = 0.6f), CircleShape)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(100.dp)
                            .background(EasylungaGreen, CircleShape)
                            .clickable { onStart() }
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🛒", fontSize = 28.sp)
                            Text("START", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Tap the circle to start",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "or hold your phone near\nthe NFC tag at the entrance",
                fontSize = 15.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}
