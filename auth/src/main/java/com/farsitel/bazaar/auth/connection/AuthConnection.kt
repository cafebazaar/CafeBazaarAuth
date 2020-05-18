package com.farsitel.bazaar.auth.connection

import android.content.Context
import com.farsitel.bazaar.auth.CafeSingInCallback
import com.farsitel.bazaar.auth.model.CafeSignInAccount

abstract class AuthConnection(context: Context) {
    abstract fun getLastAccountId(callback: CafeSingInCallback)
    abstract fun getLastAccountIdSync(): CafeSignInAccount?
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