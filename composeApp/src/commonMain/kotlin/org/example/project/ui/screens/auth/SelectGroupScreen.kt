package org.example.project.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.example.project.i18n.LocalAppLanguage
import org.example.project.i18n.stringsFor
import org.example.project.ui.theme.LocalSyncSafeColors
import org.example.project.ui.theme.EmergencyRed

@Composable
fun SelectGroupScreen(
    onCreateGroup: (String) -> Unit,
    onJoinGroup: (String) -> Unit,
    onLogout: () -> Unit
) {
    var groupName by remember { mutableStateOf("") }
    var inviteCode by remember { mutableStateOf("") }
    val extra = LocalSyncSafeColors.current
    val s = stringsFor(LocalAppLanguage.current)

    Box(
        modifier = Modifier.fillMaxSize().background(extra.authGradient)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp).systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    s.youreLoggedIn,
                    style = MaterialTheme.typography.titleMedium,
                    color = extra.authOnGradient
                )
                TextButton(onClick = onLogout) {
                    Text(s.logOut, color = EmergencyRed)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                s.notPartOfCircle,
                style = MaterialTheme.typography.headlineSmall,
                color = extra.authOnGradient,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // --- CREATE ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = extra.authOnGradient.copy(alpha = 0.1f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, extra.authOnGradient.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(s.createACircle, fontWeight = FontWeight.Bold, color = extra.authOnGradient)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = groupName,
                        onValueChange = { groupName = it },
                        label = { Text(s.circleName, color = extra.authOnGradientMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = extra.authOnGradient, unfocusedTextColor = extra.authOnGradient,
                            focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = extra.authInputBorder
                        ),
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onCreateGroup(groupName) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = extra.authButtonBackground, contentColor = extra.authButtonForeground),
                        enabled = groupName.isNotBlank()
                    ) { Text(s.createButton) }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // --- JOIN ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = extra.authOnGradient.copy(alpha = 0.1f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, extra.authOnGradient.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(s.joinACircle, fontWeight = FontWeight.Bold, color = extra.authOnGradient)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inviteCode,
                        onValueChange = { inviteCode = it.uppercase() },
                        label = { Text(s.inviteCodeLabel, color = extra.authOnGradientMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = extra.authOnGradient, unfocusedTextColor = extra.authOnGradient,
                            focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = extra.authInputBorder
                        ),
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onJoinGroup(inviteCode) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = extra.authButtonBackground, contentColor = extra.authButtonForeground),
                        enabled = inviteCode.isNotBlank()
                    ) { Text(s.joinButton) }
                }
            }
        }
    }
}
