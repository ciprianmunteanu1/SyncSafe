package org.example.project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.model.Group
import org.example.project.ui.components.MemberCard
import org.example.project.ui.theme.EmergencyRed
import org.example.project.ui.theme.SafeGreen

@Composable
fun HomeScreen(
    group: Group?,
    onSafeClick: () -> Unit,
    onNeedHelpClick: () -> Unit,
    onCrisisModeClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // CRISIS MODE BANNER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(EmergencyRed)
                .clickable { onCrisisModeClick() }
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🚨 ENTER CRISIS MODE 🚨",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (group != null) {
            // Header: Nume grup + cod invitare
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = group.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Invite code: ${group.inviteCode} (Tap to copy)",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Membri
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(group.members) { member ->
                    MemberCard(member = member)
                }
            }
        } else {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Nu sunteți într-un grup momentan.")
            }
        }

        // Butoane Rapide Jos
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onSafeClick,
                colors = ButtonDefaults.buttonColors(containerColor = SafeGreen),
                modifier = Modifier.weight(1f).height(64.dp)
            ) {
                Text("I'M SAFE", style = MaterialTheme.typography.titleLarge)
            }
            Button(
                onClick = onNeedHelpClick,
                colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                modifier = Modifier.weight(1f).height(64.dp)
            ) {
                Text("NEED HELP", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}
