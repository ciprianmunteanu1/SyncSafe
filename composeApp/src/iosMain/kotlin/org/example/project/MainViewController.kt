package org.example.project

import androidx.compose.ui.window.ComposeUIViewController

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.example.project.data.GroupRepository
import org.example.project.platform.IosLocationService

fun MainViewController() = ComposeUIViewController {
    val locationService = remember { IosLocationService() }
    
    LaunchedEffect(Unit) {
        locationService.startTracking { lat, lng ->
            launch(Dispatchers.IO) {
                GroupRepository.updateMyLocation(lat, lng)
            }
        }
    }
    
    App()
}