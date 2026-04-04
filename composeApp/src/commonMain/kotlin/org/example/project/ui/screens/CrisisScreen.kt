package org.example.project.ui.screens

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.ui.components.AlertButton

@Composable
fun CrisisScreen() {
    val infiniteTransition = rememberInfiniteTransition()
    val bgColor by infiniteTransition.animateColor(
        initialValue = Color(0xFF3E0000), // Dark red
        targetValue = Color(0xFF6B0000), // Slightly brighter red
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "CRISIS MODE",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Are you safe?",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            AlertButton(
                text = "I'M SAFE",
                icon = "✅",
                color = Color(0xFF4CAF50), // Green
                onClick = { /* TODO: Update status to SAFE */ }
            )

            Spacer(modifier = Modifier.height(24.dp))

            AlertButton(
                text = "NEED HELP",
                icon = "🆘",
                color = Color(0xFFF44336), // Red
                pulse = true,
                onClick = { /* TODO: Update status to NEEDS_HELP and alert group */ }
            )

            Spacer(modifier = Modifier.height(64.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = { /* TODO: Open checklist */ },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("📋 Checklist")
                }

                OutlinedButton(
                    onClick = { /* TODO: Open guide */ },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("📖 Guide")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* TODO: Call emergency */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.5f))
            ) {
                Text("📞 Call 112")
            }
        }
    }
}
