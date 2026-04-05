package org.example.project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.example.project.model.Group
import org.example.project.ui.components.MemberCard
import org.example.project.ui.theme.EmergencyRed
import org.example.project.ui.theme.SafeGreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.ui.semantics.Role

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    group: Group?,
    joinedGroups: Map<String, String>,
    onSwitchGroup: (String) -> Unit,
    onCreateOrJoin: () -> Unit,
    onSafeClick: () -> Unit,
    onNeedHelpClick: () -> Unit,
    onOnMyWayClick: () -> Unit,
    onCrisisModeClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // CRISIS MODE BANNER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(EmergencyRed)
                .clickable(onClick = onCrisisModeClick)
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

        // --- Circles Swapper ---
        if (joinedGroups.isNotEmpty()) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(joinedGroups.entries.toList()) { (inviteCode, groupName) ->
                        val isActive = group?.inviteCode == inviteCode
                        FilterChip(
                            selected = isActive,
                            onClick = { if (!isActive) onSwitchGroup(inviteCode) },
                            label = { Text(groupName, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }

                    item {
                        IconButton(
                            onClick = onCreateOrJoin,
                            modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), shape = MaterialTheme.shapes.small)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Add Circle", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
        // --- End Swapper ---

        if (group != null) {
            val clipboardManager = LocalClipboardManager.current
            
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.clickable {
                        clipboardManager.setText(AnnotatedString(group.inviteCode))
                    }
                )
            }

            // Membri
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(group.members) { member ->
                    MemberCard(member = member)
                }
            }

            // Butoane Rapide Jos (Pyramid UI)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top level of pyramid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = onSafeClick,
                        colors = ButtonDefaults.buttonColors(containerColor = SafeGreen),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f).height(64.dp)
                    ) {
                        Text("I'M SAFE", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onOnMyWayClick,
                        colors = ButtonDefaults.buttonColors(containerColor = org.example.project.ui.theme.OnTheWayBlue),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f).height(64.dp)
                    ) {
                        Text("ON MY WAY", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    }
                }
                
                // Bottom level of pyramid
                Button(
                    onClick = onNeedHelpClick,
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(64.dp)
                ) {
                    Text("NEED HELP", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("You do not have an active Circle.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onCreateOrJoin) {
                        Text("Create or Join a Circle")
                    }
                }
            }
        }
    }
}
