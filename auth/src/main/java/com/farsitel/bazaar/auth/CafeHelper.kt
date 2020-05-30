package com.farsitel.bazaar.auth

import android.content.Context
import com.farsitel.bazaar.auth.util.getPackageInfo
import com.farsitel.bazaar.auth.util.versionCodeSDKAware
import com.farsitel.bazaar.auth.view.CafeInstallerActivity

object CafeHelper {

    private const val BAZAAR_PACKAGE_NAME = "com.farsitel.bazaar"
    private const val BAZAAR_WITH_AUTH_VERSION = 801300

    fun isBazaarInstalledOnDevice(context: Context): Boolean =
        getPackageInfo(context, BAZAAR_PACKAGE_NAME) != null

    fun isBazaarNeedToUpdate(context: Context): Boolean =
        getBazaarVersion(context) >= BAZAAR_WITH_AUTH_VERSION

    fun showInstallBazaarViewIfNeeded(context: Context) {
        CafeInstallerActivity.startCafeInstallerActivityForInstallBazaar(context)
    }

    fun showUpdateBazaarViewIfNeeded(context: Context) {
        CafeInstallerActivity.startCafeInstallerActivityForUpdateBazaar(context)
    }

    private fun getBazaarVersion(context: Context) = getPackageInfo(
        context,
        BAZAAR_PACKAGE_NAME
    )?.versionCodeSDKAware ?: -1
}