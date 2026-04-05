package org.example.project.platform

import platform.AVFoundation.*
import platform.AudioToolbox.AudioServicesPlayAlertSound
import platform.AudioToolbox.kSystemSoundID_Vibrate
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.cinterop.ExperimentalForeignApi

actual class EmergencyHardware actual constructor() {
    
    @OptIn(DelicateCoroutinesApi::class, ExperimentalForeignApi::class)
    actual fun triggerSOSAlarm() {
        try {
            // Vibrate
            AudioServicesPlayAlertSound(kSystemSoundID_Vibrate)

            // Camera Flashlight
            val device = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo)
            if (device != null && device.hasTorch) {
                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        for (i in 0..2) {
                            device.lockForConfiguration(null)
                            device.setTorchMode(AVCaptureTorchModeOn)
                            device.unlockForConfiguration()
                            
                            delay(500)
                            
                            device.lockForConfiguration(null)
                            device.setTorchMode(AVCaptureTorchModeOff)
                            device.unlockForConfiguration()
                            
                            delay(300)
                        }
                    } catch (e: Exception) {
                        // ignore
                    }
                }
            }
        } catch (e: Exception) {
            // catch all
        }
    }
}
