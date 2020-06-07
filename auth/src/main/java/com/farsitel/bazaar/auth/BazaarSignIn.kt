package com.farsitel.bazaar.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import com.farsitel.bazaar.auth.callback.BazaarSingInCallback
import com.farsitel.bazaar.auth.connection.AuthConnection.Companion.getAuthConnection
import com.farsitel.bazaar.auth.model.BazaarSignInAccount
import com.farsitel.bazaar.auth.model.BazaarSignInOptions

class BazaarSignIn {

    companion object {

        @JvmStatic
        fun getLastSignedInAccount(
            context: Context,
            owner: LifecycleOwner,
            callback: BazaarSingInCallback
        ) {
            getAuthConnection(context).getLastAccountId(owner, callback)
        }

        @JvmStatic
        fun getLastSignedInAccountSync(
            context: Context,
            owner: LifecycleOwner
        ): BazaarSignInAccount? {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                throw IllegalStateException("Can't call this method on UI thread.")
            }
            return getAuthConnection(context).getLastAccountIdSync(owner)
        }

        @JvmStatic
        fun getClient(activity: Activity, signInOption: BazaarSignInOptions) =
            BazaarSignInClient(signInOption, activity)

        @JvmStatic
        fun getSignedInAccountFromIntent(data: Intent?) =
            BazaarSignInResult.getAccountFromIntent(data)
    }
}