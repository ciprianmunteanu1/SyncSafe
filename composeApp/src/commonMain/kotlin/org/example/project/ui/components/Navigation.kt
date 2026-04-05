package org.example.project.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.example.project.data.GroupRepository
import org.example.project.data.AuthManager
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.CircularProgressIndicator
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.Clock
import org.example.project.i18n.AppLanguage
import org.example.project.i18n.LocalAppLanguage
import org.example.project.i18n.stringsFor
import org.example.project.ui.screens.*
import org.example.project.ui.screens.auth.WelcomeScreen
import org.example.project.ui.screens.auth.CreateAccountScreen
import org.example.project.ui.screens.auth.LoginScreen
import org.example.project.ui.screens.auth.SelectGroupScreen
import org.example.project.data.AuthRepository

@Composable
fun Navigation(
    isDarkMode: Boolean = true,
    onToggleDarkMode: (Boolean) -> Unit = {},
    currentLanguage: AppLanguage = AppLanguage.EN,
    onChangeLanguage: (AppLanguage) -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val scope = rememberCoroutineScope()

    val group by GroupRepository.group.collectAsState()
    val alerts by GroupRepository.alerts.collectAsState()

    val s = stringsFor(LocalAppLanguage.current)

    val startDestination = "welcome"
    var currentCrisisType by remember { mutableStateOf<String?>(null) }

    // GLOBAL SOS STATE
    var incomingSosAlert by remember { mutableStateOf<org.example.project.model.Alert?>(null) }
    var lastAlertTimestamp by remember { mutableStateOf(kotlin.time.Clock.System.now().toEpochMilliseconds()) }

    LaunchedEffect(alerts) {
        val myMemberId = org.example.project.data.GroupRepository.me?.id
        val newAlerts = alerts.filter { it.timestamp > lastAlertTimestamp }

        if (newAlerts.isNotEmpty()) {
            lastAlertTimestamp = newAlerts.maxOf { it.timestamp }

            // Orice NEEDS_HELP de la altcineva din grup declanseaza popup + hardware
            val latestSos = newAlerts.lastOrNull { alert ->
                alert.type == org.example.project.model.AlertType.NEEDS_HELP &&
                alert.memberId != myMemberId
            }
            if (latestSos != null) {
                incomingSosAlert = latestSos
                org.example.project.platform.EmergencyHardware().triggerSOSAlarm()
            }
        }
    }

    // Routes where bottom nav should be hidden
    val hideBottomNav = listOf("welcome", "login", "register", "select_group", "crisis")

    Scaffold(
        bottomBar = {
            if (currentRoute !in hideBottomNav) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationRoute ?: "welcome") {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("welcome") {
                WelcomeScreen(
                    onNavigateToCreate = { navController.navigate("register") },
                    onNavigateToLogin = { navController.navigate("login") },
                    onAutoLoginSuccess = { route -> 
                        navController.navigate(route) { popUpTo("welcome") { inclusive = true } }
                    }
                )
            }
            composable("register") {
                CreateAccountScreen(
                    onBack = { navController.popBackStack() },
                    onSubmit = { userName, password, groupName, inviteCode -> 
                        val res = AuthRepository.register(userName, password)
                        if (res.isSuccess) {
                            AuthManager.saveSession(userName, password)
                            
                            val groupRes = if (groupName.isNotBlank()) {
                                GroupRepository.createGroup(groupName)
                            } else {
                                GroupRepository.joinGroup(inviteCode)
                            }
                            
                            if (groupRes.isSuccess) {
                                navController.navigate("home") { popUpTo("register") { inclusive = true } }
                                Result.success(true)
                            } else {
                                Result.failure(groupRes.exceptionOrNull() ?: Exception("Circle creation/join failed."))
                            }
                        } else {
                            Result.failure(res.exceptionOrNull() ?: Exception("Unknown error"))
                        }
                    }
                )
            }
            composable("login") {
                LoginScreen(
                    onBack = { navController.popBackStack() },
                    onSubmit = { userName, password -> 
                        val res = AuthRepository.login(userName, password)
                        if (res.isSuccess) {
                            AuthManager.saveSession(userName, password)
                            
                            val codeToUse = res.getOrNull()?.lastActiveGroup ?: res.getOrNull()?.joinedGroups?.keys?.firstOrNull()
                            
                            if (!codeToUse.isNullOrBlank()) {
                                val groupRes = GroupRepository.connectToGroup(codeToUse)
                                if (groupRes.isSuccess) {
                                    navController.navigate("home") { popUpTo("login") { inclusive = true } }
                                    Result.success(true)
                                } else {
                                    Result.failure(groupRes.exceptionOrNull() ?: Exception("Failed to enter Circle"))
                                }
                            } else {
                                navController.navigate("home") { popUpTo("login") { inclusive = true } }
                                Result.success(true)
                            }
                        } else {
                            Result.failure(res.exceptionOrNull() ?: Exception("Login failed."))
                        }
                    }
                )
            }
            composable("select_group") {
                SelectGroupScreen(
                    onCreateGroup = { name -> 
                        scope.launch {
                            if (GroupRepository.createGroup(name).isSuccess) {
                                navController.navigate("home") { popUpTo("select_group") { inclusive = true } }
                            }
                        }
                    },
                    onJoinGroup = { code -> 
                        scope.launch {
                            if (GroupRepository.joinGroup(code).isSuccess) {
                                navController.navigate("home") { popUpTo("select_group") { inclusive = true } }
                            }
                        }
                    },
                    onLogout = {
                        AuthManager.clearSession()
                        AuthRepository.logout()
                        navController.navigate("welcome") { popUpTo("select_group") { inclusive = true } }
                    }
                )
            }
            composable("home") { 
                HomeScreen(
                    group = group,
                    joinedGroups = AuthRepository.currentUser?.joinedGroups ?: emptyMap(),
                    onSwitchGroup = { inviteCode ->
                        scope.launch { GroupRepository.switchGroup(inviteCode) }
                    },
                    onCreateOrJoin = { navController.navigate("select_group") },
                    onSafeClick = { scope.launch { GroupRepository.updateMyStatus(org.example.project.model.MemberStatus.SAFE) } },
                    onNeedHelpClick = { scope.launch { GroupRepository.updateMyStatus(org.example.project.model.MemberStatus.NEEDS_HELP) } },
                    onOnMyWayClick = { scope.launch { GroupRepository.updateMyStatus(org.example.project.model.MemberStatus.ON_THE_WAY) } },
                    onCrisisModeClick = { 
                        navController.navigate("crisis") 
                    }
                ) 
            }
            composable("crisis") {
                CrisisScreen(
                    crisisType = currentCrisisType,
                    onBack = { navController.popBackStack() },
                    onSafeClick = {
                        scope.launch { GroupRepository.updateMyStatus(org.example.project.model.MemberStatus.SAFE) }
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    onNeedHelpClick = { type ->
                        currentCrisisType = type
                        scope.launch { GroupRepository.updateMyStatus(org.example.project.model.MemberStatus.NEEDS_HELP, type) }
                        // Hardware-ul se declanșează prin LaunchedEffect când Firebase confirmă alerta
                    }
                )
            }
            composable("status") { 
                StatusScreen(
                    onStatusClick = { status -> 
                        scope.launch { GroupRepository.updateMyStatus(status) } 
                    },
                    onLogoutClick = {
                        AuthManager.clearSession()
                        AuthRepository.logout()
                        navController.navigate("welcome") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                ) 
            }
            composable("map") { 
                group?.let { g ->
                    MapScreen(
                        members = g.members,
                        meetingPoints = g.meetingPoints,
                        onSetMeetingPoint = { lat, lng, name -> 
                            scope.launch {
                                val randomPart = (1..6).map { "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"[Random.nextInt(36)] }.joinToString("")
                                val newId = "meet_" + Clock.System.now().toEpochMilliseconds() + "_" + randomPart
                                GroupRepository.addMeetingPoint(org.example.project.model.MeetingPoint(newId, lat, lng, name))
                            }
                        },
                        onDeleteMeetingPoint = { pointId ->
                            scope.launch {
                                GroupRepository.removeMeetingPoint(pointId)
                            }
                        }
                    ) 
                } ?: run {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
            composable("feed") { ActivityFeedScreen(alerts = alerts) }
            composable("guide") { OfflineGuideScreen() }
            composable("checklist") { ChecklistScreen() }
            composable("settings") {
                SettingsScreen(
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = onToggleDarkMode,
                    currentLanguage = currentLanguage,
                    onChangeLanguage = onChangeLanguage,
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        AuthManager.clearSession()
                        AuthRepository.logout()
                        navController.navigate("welcome") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }
        }
    }

    if (incomingSosAlert != null) {
        val alert = incomingSosAlert!!
        val isCrisisMode = alert.message.contains("EMERGENCY:")
        val crisisType = if (isCrisisMode) alert.message.substringAfter("EMERGENCY:").removeSuffix("!").trim() else null

        AlertDialog(
            onDismissRequest = { /* Nu se inchide prin click exterior */ },
            containerColor = androidx.compose.ui.graphics.Color(0xFF8B0000),
            titleContentColor = androidx.compose.ui.graphics.Color.White,
            textContentColor = androidx.compose.ui.graphics.Color.White,
            title = { Text(s.sosEmergencyTitle, fontWeight = FontWeight.ExtraBold) },
            text = {
                androidx.compose.foundation.layout.Column {
                    Text(
                        text = s.sosHasEmergency.replace("%s", alert.memberName),
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.ui.graphics.Color.White
                    )
                    if (crisisType != null) {
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = s.sosEmergencyType.replace("%s", crisisType),
                            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                            color = androidx.compose.ui.graphics.Color.White
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        incomingSosAlert = null
                        navController.navigate("map") {
                            popUpTo(navController.graph.startDestinationRoute ?: "welcome") { inclusive = false }
                        }
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.ui.graphics.Color.White,
                        contentColor = androidx.compose.ui.graphics.Color.Black
                    )
                ) {
                    Text(s.sosViewOnMap, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { incomingSosAlert = null },
                    colors = androidx.compose.material3.ButtonDefaults.textButtonColors(contentColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f))
                ) {
                    Text(s.sosUnderstood, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title)
    }
}
