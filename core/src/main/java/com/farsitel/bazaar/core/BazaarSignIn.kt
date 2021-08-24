package com.farsitel.bazaar.core

import android.content.Context
import android.content.Intent
import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import com.farsitel.bazaar.BazaarResponse
import com.farsitel.bazaar.core.callback.BazaarSignInCallback
import com.farsitel.bazaar.core.connection.AuthConnection.Companion.getAuthConnection
import com.farsitel.bazaar.core.model.BazaarSignInAccount
import com.farsitel.bazaar.core.model.BazaarSignInOptions
import com.farsitel.bazaar.thread.MainThread

object BazaarSignIn {

    @JvmStatic
    fun getLastSignedInAccount(
        context: Context,
        owner: LifecycleOwner?,
        callback: BazaarSignInCallback
    ) {
        val mainThread = MainThread()
        getAuthConnection(context).getLastAccountId(owner, callback, mainThread)
    }

    @JvmStatic
    fun getLastSignedInAccountSync(
        context: Context,
        owner: LifecycleOwner?
    ): BazaarResponse<BazaarSignInAccount>? {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw IllegalStateException("Can't call this method on UI thread.")
        }
        return getAuthConnection(context).getLastAccountIdSync(owner)
    }

    @JvmStatic
    fun getClient(context: Context, signInOption: BazaarSignInOptions): BazaarSignInClient {
        return BazaarSignInClient(signInOption, context)
    }

    @JvmStatic
    fun getSignedInAccountFromIntent(data: Intent?): BazaarSignInAccount? {
        return BazaarSignInResult.getAccountFromIntent(data)
    }

    @JvmStatic
    fun disconnect(context: Context) {
        getAuthConnection(context).disconnect()
    }
}