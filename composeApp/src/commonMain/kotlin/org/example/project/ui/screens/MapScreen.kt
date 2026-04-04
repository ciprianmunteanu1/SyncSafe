package org.example.project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.project.model.MeetingPoint
import org.example.project.model.Member
import org.example.project.model.MemberStatus

@Composable
fun MapScreen(
    members: List<Member>,
    currentMeetingPoint: MeetingPoint?,
    onSetMeetingPoint: (Double, Double, String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Group Location",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Meeting Point Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Meeting Point",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (currentMeetingPoint != null) {
                    Text(text = "📍 ${currentMeetingPoint.name}", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = "Lat: ${currentMeetingPoint.latitude}, Lng: ${currentMeetingPoint.longitude}",
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    Text(text = "No meeting point set.", style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { showDialog = true }) {
                    Text(if (currentMeetingPoint == null) "Set Meeting Point" else "Change Meeting Point")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Members on the way
        val onTheWayMembers = members.filter { it.status == MemberStatus.ON_THE_WAY }
        if (currentMeetingPoint != null && onTheWayMembers.isNotEmpty()) {
            Text(
                text = "🏃 On The Way:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            onTheWayMembers.forEach { member ->
                Text(text = "• ${member.name}", modifier = Modifier.padding(start = 8.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Coordinates List (Simplified MVP map)
        Text(
            text = "Members Coordinates",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(members, key = { it.id }) { member ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = member.status.emoji)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = member.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            if (member.hasLocation) {
                                Text(
                                    text = "Lat: ${member.latitude}\nLng: ${member.longitude}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Text(
                                    text = "Location unavailable",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                        // Tag for status label
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = member.status.label,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        SetMeetingPointDialog(
            onDismiss = { showDialog = false },
            onConfirm = { lat, lng, name ->
                onSetMeetingPoint(lat, lng, name)
                showDialog = false
            }
        )
    }
}

@Composable
fun SetMeetingPointDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double, Double, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var latStr by remember { mutableStateOf("") }
    var lngStr by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Set Meeting Point",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Location Name") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = latStr,
                    onValueChange = { latStr = it },
                    label = { Text("Latitude") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = lngStr,
                    onValueChange = { lngStr = it },
                    label = { Text("Longitude") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val lat = latStr.toDoubleOrNull() ?: 0.0
                    val lng = lngStr.toDoubleOrNull() ?: 0.0
                    onConfirm(lat, lng, name.ifBlank { "Meeting Point" })
                }
            ) {
                Text("Set")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
