package com.farsitel.bazaar.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent

internal fun Context.safeStartActivity(intent: Intent) {
    try {
        startActivity(intent)
    } catch (ane: ActivityNotFoundException) {
        InAppLoginLogger.logError("activity not found")
    }
}