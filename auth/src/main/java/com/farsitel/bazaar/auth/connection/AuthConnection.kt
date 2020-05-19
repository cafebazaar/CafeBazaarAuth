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

    companion object {
        const val PACKAGE_NAME_KEY = "packageName"

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