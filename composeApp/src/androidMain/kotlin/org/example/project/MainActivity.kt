package org.example.project

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.project.data.GroupRepository
import org.example.project.platform.AndroidLocationService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        // Configurare cerere de permisiuni GPS și pornire automată serviciu de background
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val fineAllowed = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)
            val coarseAllowed = permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
            
            if (fineAllowed || coarseAllowed) {
                // Pornim GPS-ul hardware și conectăm direct la repo-ul multiplatform
                val locationService = AndroidLocationService(this)
                locationService.startTracking { lat, lng ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        GroupRepository.updateMyLocation(lat, lng)
                    }
                }
            }
        }

        // Cerem instant permisiunile la deschiderea aplicației (Dacă nu le avem deja)
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}