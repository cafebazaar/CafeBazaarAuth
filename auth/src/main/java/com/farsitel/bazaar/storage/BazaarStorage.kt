package com.farsitel.bazaar.storage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import com.farsitel.bazaar.auth.BazaarSignInClient
import com.farsitel.bazaar.auth.BazaarSignInResult
import com.farsitel.bazaar.auth.callback.BazaarSignInCallback
import com.farsitel.bazaar.auth.connection.AuthConnection
import com.farsitel.bazaar.auth.model.BazaarSignInAccount
import com.farsitel.bazaar.auth.model.BazaarSignInOptions

object BazaarStorage {

    @JvmStatic
    fun getSavedData(
        context: Context,
        owner: LifecycleOwner,
        callback: BazaarSignInCallback
    ) {
        AuthConnection.getAuthConnection(context).getLastAccountId(owner, callback)
    }

    @JvmStatic
    fun getSavedDataSync(
        context: Context,
        owner: LifecycleOwner
    ): BazaarSignInAccount? {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw IllegalStateException("Can't call this method on UI thread.")
        }
        return AuthConnection.getAuthConnection(context).getLastAccountIdSync(owner)
    }

    @JvmStatic
    fun getClient(activity: Activity, signInOption: BazaarSignInOptions): BazaarSignInClient {
        return BazaarSignInClient(signInOption, activity)
    }

    @JvmStatic
    fun getSignedInAccountFromIntent(data: Intent?): BazaarSignInAccount? {
        return BazaarSignInResult.getAccountFromIntent(data)
    }
}