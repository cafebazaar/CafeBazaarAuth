package com.farsitel.bazaar.auth.util

import android.util.Log

object InAppLoginLogger {
    var mDebugLog = false
    val mDebugTag = "inAppLogin"
    fun logDebug(msg: String) {
        if (mDebugLog) Log.d(mDebugTag, msg)
    }

    fun logError(msg: String?) {
        Log.e(mDebugTag, "In-app login error: $msg")
    }

    fun logWarn(msg: String) {
        Log.w(mDebugTag, "In-app login warning: $msg")
    }
}