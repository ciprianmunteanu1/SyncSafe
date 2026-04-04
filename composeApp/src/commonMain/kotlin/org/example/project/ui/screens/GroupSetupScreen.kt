package org.example.project.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun GroupSetupScreen(
    onCreateGroup: (String, String) -> Unit, // userName, groupName
    onJoinGroup: (String, String) -> Unit // userName, inviteCode
) {
    var userName by remember { mutableStateOf("") }
    var groupName by remember { mutableStateOf("") }
    var inviteCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to SyncSafe",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("Numele tău") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Card pentru creare grup
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Crează un grup nou", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = { Text("Numele grupului") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onCreateGroup(userName, groupName) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = userName.isNotBlank() && groupName.isNotBlank()
                ) {
                    Text("CREAZĂ GRUP")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Card pentru aderare
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Alătură-te unui grup", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = inviteCode,
                    onValueChange = { inviteCode = it.uppercase() },
                    label = { Text("Cod de invitare (ex: A3K9PZ)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onJoinGroup(userName, inviteCode) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = userName.isNotBlank() && inviteCode.isNotBlank()
                ) {
                    Text("ALĂTURĂ-TE")
                }
            }
        }
    }
}
