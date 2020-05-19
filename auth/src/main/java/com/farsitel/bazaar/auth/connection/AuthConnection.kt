package com.farsitel.bazaar.auth.connection

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import com.farsitel.bazaar.auth.callback.CafeSingInCallback
import com.farsitel.bazaar.auth.model.CafeSignInAccount
import com.farsitel.bazaar.auth.model.CafeSignInOptions

internal abstract class AuthConnection(private val context: Context) {
    abstract fun getLastAccountId(owner: LifecycleOwner, callback: CafeSingInCallback)
    abstract fun getLastAccountIdSync(owner: LifecycleOwner): CafeSignInAccount?

    private val ailAction = "ir.cafebazaar.intent.action.IAL"
    private val ialActionUri = "bazaar://inapplogin"

    fun getSignInIntentWithScope(signInOption: CafeSignInOptions) =
        Intent(ailAction, Uri.parse(ialActionUri))
            .apply {
                setPackage("com.farsitel.bazaar")
                putExtra(PACKAGE_NAME_KEY, context.packageName)
                putExtra(
                    PERMISSION_SCOPE_KEY,
                    signInOption.getScopes().map { it.ordinal }.toIntArray()
                )
            }

    companion object {
        const val PACKAGE_NAME_KEY = "packageName"
        private const val PERMISSION_SCOPE_KEY = "permissionScope"


        private lateinit var authConnection: AuthConnection
        private val lockObject = Object()

        fun getAuthConnection(context: Context): AuthConnection {
            if (!::authConnection.isInitialized) {
                synchronized(lockObject) {
                    if (!::authConnection.isInitialized) {
                        authConnection = ReceiverAuthConnection(context)
                    }
                }
            }
            return authConnection
        }
    }
}