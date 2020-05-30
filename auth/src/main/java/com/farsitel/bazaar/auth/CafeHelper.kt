package com.farsitel.bazaar.auth

import android.content.Context
import com.farsitel.bazaar.auth.security.Security
import com.farsitel.bazaar.auth.util.getPackageInfo
import com.farsitel.bazaar.auth.util.versionCodeSDKAware
import com.farsitel.bazaar.auth.view.CafeInstallerActivity

object CafeHelper {

    private const val BAZAAR_PACKAGE_NAME = "com.farsitel.bazaar"
    private const val BAZAAR_WITH_AUTH_VERSION = 801300

    fun isBazaarInstalledOnDevice(context: Context): Boolean =
        getPackageInfo(context, BAZAAR_PACKAGE_NAME) != null &&
                Security.verifyBazaarIsInstalled(context)

    fun isBazaarNeedToUpdate(context: Context): Boolean =
        getBazaarVersion(context) >= BAZAAR_WITH_AUTH_VERSION

    fun showInstallBazaarView(context: Context) {
        CafeInstallerActivity.startCafeInstallerActivityForInstallBazaar(context)
    }

    fun showUpdateBazaarView(context: Context) {
        CafeInstallerActivity.startCafeInstallerActivityForUpdateBazaar(context)
    }

    private fun getBazaarVersion(context: Context) = getPackageInfo(
        context,
        BAZAAR_PACKAGE_NAME
    )?.versionCodeSDKAware ?: -1
}