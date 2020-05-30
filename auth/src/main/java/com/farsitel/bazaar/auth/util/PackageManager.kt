package com.farsitel.bazaar.auth.util

import android.content.Context
import android.content.pm.PackageInfo
import android.os.Build


internal fun getPackageInfo(context: Context, packageName: String) = try {
    val packageManager = context.packageManager
    packageManager.getPackageInfo(packageName, 0)
} catch (e: Exception) {
    null
}

val PackageInfo.versionCodeSDKAware: Long
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            longVersionCode
        } else {
            @Suppress("DEPRECATION")
            versionCode.toLong()
        }
    }
