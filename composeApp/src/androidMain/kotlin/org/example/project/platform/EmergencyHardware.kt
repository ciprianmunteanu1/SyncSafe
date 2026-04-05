package org.example.project.platform

import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

lateinit var appContext: Context

actual class EmergencyHardware actual constructor() {
    
    @OptIn(DelicateCoroutinesApi::class)
    actual fun triggerSOSAlarm() {
        try {
            val context = appContext
            
            // Vibrate
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            if (vibrator.hasVibrator()) {
                val pattern = longArrayOf(0, 500, 200, 500, 200, 500) // SOS pattern
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(pattern, -1)
                }
            }

            // Camera Flashlight
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
            
            if (cameraManager != null) {
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        val cameraId = cameraManager.cameraIdList.firstOrNull() ?: return@launch
                        // blink 3 times
                        for (i in 0..2) {
                            cameraManager.setTorchMode(cameraId, true)
                            delay(500)
                            cameraManager.setTorchMode(cameraId, false)
                            delay(300)
                        }
                    } catch (e: Exception) {
                        // ignore if no camera or permission
                    }
                }
            }
        } catch (e: Exception) {
            // General safety catch to not crash app if device lacks hardware
        }
    }
}
