package org.example.project

import androidx.compose.runtime.Composable
import org.example.project.ui.components.Navigation
import org.example.project.ui.theme.SyncSafeTheme

@Composable
fun App() {
    SyncSafeTheme {
        Navigation()
    }
}