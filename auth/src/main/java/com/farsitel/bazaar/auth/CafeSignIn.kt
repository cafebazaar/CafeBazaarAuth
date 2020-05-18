package com.farsitel.bazaar.auth

import android.content.Context
import android.os.Looper
import com.farsitel.bazaar.auth.connection.getAuthConnection
import com.farsitel.bazaar.auth.model.CafeSignInAccount

object CafeSignIn {

    fun getLastSignedInAccount(context: Context, callback: CafeSingInCallback) {
        getAuthConnection(context).getLastAccountId(callback)
    }

    fun getLastSignedInAccountSync(context: Context): CafeSignInAccount? {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw IllegalStateException("Can't call this method in UI thread.")
        }
        return getAuthConnection(context).getLastAccountIdSync()
    }
}