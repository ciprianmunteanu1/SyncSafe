package org.example.project.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.example.project.data.AuthManager
import org.example.project.data.AuthRepository
import org.example.project.data.GroupRepository
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onAutoLoginSuccess: (String) -> Unit
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
    )

    var isSilentLoggingIn by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (AuthManager.hasValidSession()) {
            isSilentLoggingIn = true
            val un = AuthManager.getUsername()!!
            val pw = AuthManager.getPassword()!!
            val res = AuthRepository.login(un, pw)
            if (res.isSuccess) {
                val user = res.getOrNull()
                val inviteCode = user?.lastActiveGroup ?: user?.joinedGroups?.keys?.firstOrNull()
                if (inviteCode != null && GroupRepository.connectToGroup(inviteCode).isSuccess) {
                    onAutoLoginSuccess("home")
                } else {
                    onAutoLoginSuccess("home")
                }
            } else {
                AuthManager.clearSession()
                isSilentLoggingIn = false
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(100.dp).background(Color.White.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Warning, contentDescription = "Logo", modifier = Modifier.size(50.dp), tint = Color.White)
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text("SyncSafe", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Conectare inteligentă.", style = MaterialTheme.typography.titleMedium, color = Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center)
            
            Spacer(modifier = Modifier.height(48.dp))

            if (isSilentLoggingIn) {
                CircularProgressIndicator(color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Se restabilește sesiunea...", color = Color.White.copy(alpha = 0.7f))
            } else {
                Button(
                    onClick = onNavigateToLogin,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF0F2027))
                ) { Text("Log In", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onNavigateToCreate,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
                ) { Text("Create Account", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
            }
        }
    }
}
