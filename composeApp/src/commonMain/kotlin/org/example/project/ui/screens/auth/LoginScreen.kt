package org.example.project.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.example.project.i18n.LocalAppLanguage
import org.example.project.i18n.stringsFor
import org.example.project.ui.theme.LocalSyncSafeColors
import org.example.project.ui.theme.EmergencyRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onBack: () -> Unit,
    onSubmit: suspend (username: String, passwordHash: String) -> Result<Boolean>
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val extra = LocalSyncSafeColors.current
    val s = stringsFor(LocalAppLanguage.current)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(s.loginTitle, color = extra.authOnGradient) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.loginBack, tint = extra.authOnGradient)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent,
        modifier = Modifier.background(extra.authGradient)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = extra.authOnGradient.copy(alpha = 0.1f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, extra.authOnGradient.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(s.loginTitle, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = extra.authOnGradient)
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text(s.loginUsername, color = extra.authOnGradientMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = extra.authOnGradient, unfocusedTextColor = extra.authOnGradient,
                            focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = extra.authInputBorder,
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(s.loginPassword, color = extra.authOnGradientMuted) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = extra.authOnGradient, unfocusedTextColor = extra.authOnGradient,
                            focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = extra.authInputBorder,
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(errorMessage!!, color = EmergencyRed, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                errorMessage = null
                                val result = onSubmit(username.trim(), password.trim())
                                if (result.isFailure) {
                                    errorMessage = result.exceptionOrNull()?.message ?: s.loginFailed
                                }
                                isLoading = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = extra.authButtonBackground, contentColor = extra.authButtonForeground),
                        enabled = username.isNotBlank() && password.isNotBlank() && !isLoading
                    ) {
                        if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = extra.authButtonForeground)
                        else Text(s.loginButton, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
