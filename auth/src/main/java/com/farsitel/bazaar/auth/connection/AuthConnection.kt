package com.farsitel.bazaar.auth.connection

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.farsitel.bazaar.BazaarResponse
import com.farsitel.bazaar.auth.callback.BazaarSignInCallback
import com.farsitel.bazaar.auth.model.BazaarSignInAccount
import com.farsitel.bazaar.thread.MainThread

internal abstract class AuthConnection(private val context: Context) {

    abstract fun getLastAccountId(owner: LifecycleOwner?, callback: BazaarSignInCallback)
    abstract fun getLastAccountIdSync(owner: LifecycleOwner?): BazaarResponse<BazaarSignInAccount>?

    companion object {
        const val PACKAGE_NAME_KEY = "packageName"

        private lateinit var authConnection: AuthConnection
        private val lockObject = Object()

        fun getAuthConnection(context: Context): AuthConnection {
            if (!::authConnection.isInitialized) {
                synchronized(lockObject) {
                    if (!::authConnection.isInitialized) {
                        authConnection = ReceiverAuthConnection(context, MainThread())
                    }
                }
            }
            return authConnection
        }
    }
}