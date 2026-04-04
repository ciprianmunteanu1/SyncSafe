package org.example.project.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.example.project.model.Group
import org.example.project.model.Member
import org.example.project.model.MemberStatus
import org.example.project.ui.screens.HomeScreen
import org.example.project.ui.screens.GroupSetupScreen

val dummyGroup = Group(
    id = "1",
    name = "Familia Popescu",
    inviteCode = "X7B9K2",
    members = listOf(
        Member("1", "Tatăl", MemberStatus.SAFE),
        Member("2", "Mama", MemberStatus.UNKNOWN),
        Member("3", "Copilul", MemberStatus.ON_THE_WAY),
        Member("4", "Bunicul", MemberStatus.NEEDS_HELP)
    )
)

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "setup",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("setup") {
                GroupSetupScreen(
                    onCreateGroup = { _, _ -> navController.navigate("home") { popUpTo("setup") { inclusive = true } } },
                    onJoinGroup = { _, _ -> navController.navigate("home") { popUpTo("setup") { inclusive = true } } }
                )
            }
            composable("home") { 
                HomeScreen(
                    group = dummyGroup,
                    onSafeClick = { /* MVP placeholder */ },
                    onNeedHelpClick = { /* MVP placeholder */ },
                    onCrisisModeClick = { navController.navigate("crisis") }
                ) 
            }
            composable("status") { PlaceholderScreen("Status Screen") }
            composable("map") { PlaceholderScreen("Map Screen") }
            composable("feed") { PlaceholderScreen("Activity Feed Screen") }
            composable("guide") { PlaceholderScreen("Offline Guide Screen") }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = title)
    }
}
