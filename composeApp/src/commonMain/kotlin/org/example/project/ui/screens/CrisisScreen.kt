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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.selectable
import androidx.compose.ui.semantics.Role
import org.example.project.i18n.LocalAppLanguage
import org.example.project.i18n.stringsFor
import org.example.project.ui.components.AlertButton

@Composable
fun CrisisScreen(
    crisisType: String? = null,
    onBack: () -> Unit = {},
    onSafeClick: () -> Unit = {},
    onNeedHelpClick: (String) -> Unit = {}
) {
    val s = stringsFor(LocalAppLanguage.current)

    var showChecklist by remember { mutableStateOf(false) }
    var showCrisisDialog by remember { mutableStateOf(false) }
    var selectedCrisisType by remember { mutableStateOf(s.fire) }

    // Crisis options use internal keys for Firebase, display uses localized names
    val crisisKeys = listOf("Fire", "Earthquake", "Military Risk", "Flood")
    val crisisLabels = listOf(s.fire, s.earthquake, s.militaryRisk, s.flood)
    var selectedCrisisIndex by remember { mutableStateOf(0) }

    val uriHandler = LocalUriHandler.current

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
                text = s.crisisMode,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = s.areYouSafe,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            AlertButton(
                text = s.imSafe,
                icon = "✅",
                color = Color(0xFF4CAF50),
                onClick = onSafeClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            AlertButton(
                text = s.emergency,
                icon = "🆘",
                color = Color(0xFFF44336),
                pulse = true,
                onClick = { showCrisisDialog = true }
            )

            Spacer(modifier = Modifier.height(64.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = { showChecklist = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text(s.checklist)
                }

                OutlinedButton(
                    onClick = { 
                        val query = if (crisisType == "Military Risk") "civil defense bunker" else "emergency hospital"
                        uriHandler.openUri("https://www.google.com/maps/search/?api=1&query=$query")
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    val label = if (crisisType == "Military Risk") s.bunkers else s.hospitals
                    Text(label)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* TODO: Call emergency */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.5f))
            ) {
                Text(s.call112)
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(
                onClick = onBack,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.7f))
            ) {
                Text(s.backToHome)
            }
        }

        // ─── Dialogs (trebuie sa fie in Box ca sa apara deasupra continutului) ───

        if (showCrisisDialog) {
            AlertDialog(
                onDismissRequest = { showCrisisDialog = false },
                title = { Text(s.confirmEmergency) },
                text = {
                    Column {
                        Text(s.confirmEmergencyBody)
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(Modifier.selectableGroup()) {
                            crisisLabels.forEachIndexed { index, label ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .selectable(
                                            selected = (index == selectedCrisisIndex),
                                            onClick = { selectedCrisisIndex = index },
                                            role = Role.RadioButton
                                        )
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = (index == selectedCrisisIndex),
                                        onClick = null
                                    )
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(start = 16.dp)
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showCrisisDialog = false
                            onNeedHelpClick(crisisKeys[selectedCrisisIndex])
                        }
                    ) {
                        Text(s.sendEmergency, color = Color(0xFFF44336), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCrisisDialog = false }) {
                        Text(s.cancel)
                    }
                }
            )
        }

        if (showChecklist) {
            val generalRules = listOf(
                s.ruleStayCalm,
                s.ruleAssessSituation,
                s.ruleCall112,
                s.ruleAlertGroup
            )

            val specificRules = when (crisisType) {
                "Fire" -> listOf(s.fireRule1, s.fireRule2, s.fireRule3)
                "Earthquake" -> listOf(s.quakeRule1, s.quakeRule2, s.quakeRule3, s.quakeRule4)
                "Military Risk" -> listOf(s.milRule1, s.milRule2, s.milRule3)
                "Flood" -> listOf(s.floodRule1, s.floodRule2, s.floodRule3)
                else -> emptyList()
            }

            AlertDialog(
                onDismissRequest = { showChecklist = false },
                title = { Text("📋 ${s.checklist}: ${crisisType ?: "General"}") },
                text = {
                    LazyColumn {
                        items(specificRules + generalRules) { rule ->
                            var isChecked by remember { mutableStateOf(false) }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isChecked = !isChecked }
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(checked = isChecked, onCheckedChange = { isChecked = it })
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = rule, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showChecklist = false }) {
                        Text(s.understood, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

