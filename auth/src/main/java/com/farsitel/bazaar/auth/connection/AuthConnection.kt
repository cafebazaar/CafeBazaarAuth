package com.farsitel.bazaar.auth.connection

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.farsitel.bazaar.auth.callback.CafeSingInCallback
import com.farsitel.bazaar.auth.model.CafeSignInAccount

abstract class AuthConnection(context: Context) {
    abstract fun getLastAccountId(owner: LifecycleOwner, callback: CafeSingInCallback)
    abstract fun getLastAccountIdSync(owner: LifecycleOwner): CafeSignInAccount?
}

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