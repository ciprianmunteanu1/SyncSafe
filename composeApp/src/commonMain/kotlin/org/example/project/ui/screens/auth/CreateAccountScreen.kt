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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountScreen(
    onBack: () -> Unit,
    onSubmit: suspend (username: String, passwordHash: String, groupName: String, inviteCode: String) -> Result<Boolean>
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var groupName by remember { mutableStateOf("") }
    var inviteCode by remember { mutableStateOf("") }
    
    var selectedMode by remember { mutableStateOf(0) } // 0 = Create, 1 = Join
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sign Up", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent,
        modifier = Modifier.background(gradient)
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
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Create Profile", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username", color = Color.White.copy(alpha = 0.8f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF2196F3), unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            cursorColor = Color(0xFF2196F3)
                        ),
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", color = Color.White.copy(alpha = 0.8f)) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF2196F3), unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            cursorColor = Color(0xFF2196F3)
                        ),
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    TabRow(
                        selectedTabIndex = selectedMode,
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ) {
                        Tab(selected = selectedMode == 0, onClick = { selectedMode = 0 }) {
                            Text("Create Circle", modifier = Modifier.padding(16.dp))
                        }
                        Tab(selected = selectedMode == 1, onClick = { selectedMode = 1 }) {
                            Text("Join Circle", modifier = Modifier.padding(16.dp))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    if (selectedMode == 0) {
                        OutlinedTextField(
                            value = groupName,
                            onValueChange = { groupName = it },
                            label = { Text("New Circle Name", color = Color.White.copy(alpha = 0.8f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF4CAF50), unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                cursorColor = Color(0xFF4CAF50)
                            ),
                            modifier = Modifier.fillMaxWidth(), singleLine = true
                        )
                    } else {
                        OutlinedTextField(
                            value = inviteCode,
                            onValueChange = { inviteCode = it.uppercase() },
                            label = { Text("Invite Code", color = Color.White.copy(alpha = 0.8f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF9C27B0), unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                cursorColor = Color(0xFF9C27B0)
                            ),
                            modifier = Modifier.fillMaxWidth(), singleLine = true
                        )
                    }

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(errorMessage!!, color = Color(0xFFFF5252), style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                errorMessage = null
                                
                                val gn = if (selectedMode == 0) groupName.trim() else ""
                                val ic = if (selectedMode == 1) inviteCode.trim() else ""
                                
                                val result = onSubmit(username.trim(), password.trim(), gn, ic)
                                if (result.isFailure) {
                                    errorMessage = result.exceptionOrNull()?.message ?: "Registration failed."
                                }
                                isLoading = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                        enabled = username.isNotBlank() && password.isNotBlank() && (if (selectedMode == 0) groupName.isNotBlank() else inviteCode.isNotBlank()) && !isLoading
                    ) {
                        if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        else Text("Create & Enter", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
