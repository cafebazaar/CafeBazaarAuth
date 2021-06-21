package com.farsitel.bazaar.util

import android.util.Log

internal object InAppLoginLogger {

    var debugLog = false
    private const val DEBUG_TAG = "BazaarInAppLogin"

    fun logDebug(msg: String) {
        if (debugLog) {
            Log.d(DEBUG_TAG, msg)
        }
    }

    fun logError(msg: String?) {
        Log.e(DEBUG_TAG, "In-app login error: $msg")
    }

    fun logWarn(msg: String) {
        Log.w(DEBUG_TAG, "In-app login warning: $msg")
    }
}