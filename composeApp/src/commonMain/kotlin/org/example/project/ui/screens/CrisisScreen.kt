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
import org.example.project.ui.components.AlertButton

@Composable
fun CrisisScreen(
    crisisType: String? = null,
    onBack: () -> Unit = {},
    onSafeClick: () -> Unit = {},
    onNeedHelpClick: (String) -> Unit = {}
) {
    var showChecklist by remember { mutableStateOf(false) }
    var showCrisisDialog by remember { mutableStateOf(false) }
    var selectedCrisisType by remember { mutableStateOf("Incendiu") }
    val crisisOptions = listOf("Incendiu", "Cutremur", "Risc Militar", "Inundație")
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
                color = Color(0xFF4CAF50),
                onClick = onSafeClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            AlertButton(
                text = "EMERGENCY",
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
                    Text("📋 Checklist")
                }

                OutlinedButton(
                    onClick = { 
                        val query = if (crisisType == "Risc Militar") "adapost civil buncar" else "spital urgente"
                        uriHandler.openUri("https://www.google.com/maps/search/?api=1&query=$query")
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    val label = if (crisisType == "Risc Militar") "🛡️ Bunkers" else "🏥 Hospitals"
                    Text(label)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* TODO: Call emergency */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.5f))
            ) {
                Text("📞 Call 112")
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(
                onClick = onBack,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.7f))
            ) {
                Text("← Back to Home")
            }
        }

        // ─── Dialogs (trebuie sa fie in Box ca sa apara deasupra continutului) ───

        if (showCrisisDialog) {
            AlertDialog(
                onDismissRequest = { showCrisisDialog = false },
                title = { Text("🚨 Confirmă Urgența") },
                text = {
                    Column {
                        Text("Ești sigur că este o urgență reală? Selectează tipul:")
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(Modifier.selectableGroup()) {
                            crisisOptions.forEach { option ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .selectable(
                                            selected = (option == selectedCrisisType),
                                            onClick = { selectedCrisisType = option },
                                            role = Role.RadioButton
                                        )
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = (option == selectedCrisisType),
                                        onClick = null
                                    )
                                    Text(
                                        text = option,
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
                            onNeedHelpClick(selectedCrisisType)
                        }
                    ) {
                        Text("🆘 Trimite Urgența", color = Color(0xFFF44336), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCrisisDialog = false }) {
                        Text("Anulează")
                    }
                }
            )
        }

        if (showChecklist) {
            val generalRules = listOf(
                "Păstrează calmul: Menține-ți mintea limpede și analizează pericolul.",
                "Evaluează situația: Ești într-un loc sigur? Dacă nu, mută-te urgent.",
                "Sună la 112: Semnalează autoritățile dacă există victime.",
                "Alertează grupul: Așteaptă confirmarea locațiilor de la ceilalți."
            )

            val specificRules = when (crisisType) {
                "Incendiu" -> listOf(
                    "Folosește scările, evită total lifturile",
                    "Stai cât mai aproape de podea, aplecat",
                    "Acoperă nasul/gura cu o cârpă umedă"
                )
                "Cutremur" -> listOf(
                    "Adăpostește-te sub un birou/masă solidă",
                    "Îndepărtează-te de ferestre sau mobilier înalt",
                    "Așteaptă oprirea seismului înainte să ieși",
                    "Nu folosi scările în timpul zguduiturii"
                )
                "Risc Militar" -> listOf(
                    "Evacuează zona organizat dacă ieșirea e sigură",
                    "Nu declanșa echipamente electronice în proximitate",
                    "Caută cel mai apropiat adăpost civil / buncăr"
                )
                "Inundație" -> listOf(
                    "Oprește alimentarea cu gaz și electricitate din panou",
                    "Mută actele și proviziile la etajele superioare",
                    "Evită contactul cu apa stagnantă sau tulbure de afară"
                )
                else -> emptyList()
            }

            AlertDialog(
                onDismissRequest = { showChecklist = false },
                title = { Text("📋 Checklist: ${crisisType ?: "General"}") },
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
                        Text("Am înțeles", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

