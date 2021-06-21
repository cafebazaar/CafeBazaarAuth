package com.farsitel.bazaar.core

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.farsitel.bazaar.BAZAAR_PACKAGE_NAME
import com.farsitel.bazaar.core.connection.AuthConnection
import com.farsitel.bazaar.core.model.BazaarSignInOptions

class BazaarSignInClient internal constructor(
    private val signInOption: BazaarSignInOptions,
    private val context: Context
) {

    fun getSignInIntent(): Intent {
        return getSignInIntent(signInOption)
    }

    private fun getSignInIntent(signInOption: BazaarSignInOptions): Intent {
        return Intent(IAL_ACTION, Uri.parse(IAL_ACTION_URI))
            .apply {
                setPackage(BAZAAR_PACKAGE_NAME)
                putExtra(AuthConnection.PACKAGE_NAME_KEY, context.packageName)
                putExtra(
                    PERMISSION_SCOPE_KEY,
                    signInOption.getScopes().map { it.ordinal }.toIntArray()
                )
            }
    }

    companion object {
        private const val IAL_ACTION = "ir.cafebazaar.intent.action.IAL"
        private const val IAL_ACTION_URI = "bazaar://inapplogin"

        private const val PERMISSION_SCOPE_KEY = "permissionScope"
    }
}