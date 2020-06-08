package com.farsitel.bazaar.auth

import android.content.Context
import com.farsitel.bazaar.auth.security.Security
import com.farsitel.bazaar.auth.util.getPackageInfo
import com.farsitel.bazaar.auth.util.versionCodeSDKAware
import com.farsitel.bazaar.auth.view.BazaarInstallerActivity

object BazaarHelper {

    private const val BAZAAR_PACKAGE_NAME = "com.farsitel.bazaar"
    private const val BAZAAR_WITH_AUTH_VERSION = 801300

    fun isBazaarInstalledOnDevice(context: Context): Boolean {
        return getPackageInfo(context, BAZAAR_PACKAGE_NAME) != null &&
                Security.verifyBazaarIsInstalled(context)
    }

    fun isNeededToUpdateBazaar(context: Context): Boolean {
        return getBazaarVersion(context) >= BAZAAR_WITH_AUTH_VERSION
    }

    fun showInstallBazaarView(context: Context) {
        BazaarInstallerActivity.startCafeInstallerActivityForInstallBazaar(context)
    }

    fun showUpdateBazaarView(context: Context) {
        BazaarInstallerActivity.startCafeInstallerActivityForUpdateBazaar(context)
    }

    private fun getBazaarVersion(context: Context) : Long {
        return getPackageInfo(
            context,
            BAZAAR_PACKAGE_NAME
        )?.versionCodeSDKAware ?: -1
    }
}