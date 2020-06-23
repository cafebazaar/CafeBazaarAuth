package com.farsitel.bazaar.auth.util

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import java.util.*

val PackageInfo.versionCodeSDKAware: Long
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            longVersionCode
        } else {
            @Suppress("DEPRECATION")
            versionCode.toLong()
        }
    }

internal fun getPackageInfo(context: Context, packageName: String) = try {
    val packageManager = context.packageManager
    packageManager.getPackageInfo(packageName, 0)
} catch (e: Exception) {
    null
}

internal fun getAppName(context: Context) =
    getPackageInfo(context, context.packageName)?.appName(
        context,
        Locale("fa")
    )

internal fun PackageInfo.appName(context: Context, locale: Locale): String? = try {
    val applicationInfo = context.packageManager.getApplicationInfo(
        packageName,
        PackageManager.GET_META_DATA
    )

    val configuration = Configuration()
    configuration.setLocale(locale)

    val callingAppContext = context.createPackageContext(
        packageName,
        Context.CONTEXT_IGNORE_SECURITY
    )

    val updatedContext = callingAppContext.createConfigurationContext(configuration)

    if (applicationInfo.labelRes != 0) {
        updatedContext.resources.getString(applicationInfo.labelRes)
    } else {
        applicationInfo.loadLabel(context.packageManager).toString()
    }
} catch (e: Exception) {
    applicationInfo.loadLabel(context.packageManager).toString()
}
