package com.farsitel.bazaar.util.ext

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import com.farsitel.bazaar.util.InAppLoginLogger

internal fun Context.safeStartActivity(intent: Intent) {
    try {
        startActivity(intent)
    } catch (ane: ActivityNotFoundException) {
        InAppLoginLogger.logError("activity not found")
    }
}