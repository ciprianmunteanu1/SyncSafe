package org.example.project.platform

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter

import platform.Foundation.dateWithTimeIntervalSince1970

actual fun formatTimestamp(millis: Long): String {
    val date = NSDate.dateWithTimeIntervalSince1970(millis / 1000.0)
    val formatter = NSDateFormatter()
    formatter.dateFormat = "HH:mm - dd/MM/yyyy"
    return formatter.stringFromDate(date)
}
