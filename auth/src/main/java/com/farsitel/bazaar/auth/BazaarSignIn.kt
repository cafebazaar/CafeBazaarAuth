package com.farsitel.bazaar.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import com.farsitel.bazaar.BazaarResponse
import com.farsitel.bazaar.auth.callback.BazaarSignInCallback
import com.farsitel.bazaar.auth.connection.AuthConnection.Companion.getAuthConnection
import com.farsitel.bazaar.auth.model.BazaarSignInAccount
import com.farsitel.bazaar.auth.model.BazaarSignInOptions

object BazaarSignIn {

    @JvmStatic
    fun getLastSignedInAccount(
        context: Context,
        owner: LifecycleOwner?,
        callback: BazaarSignInCallback
    ) {
        getAuthConnection(context).getLastAccountId(owner, callback)
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
    fun getClient(activity: Activity, signInOption: BazaarSignInOptions): BazaarSignInClient {
        return BazaarSignInClient(signInOption, activity)
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