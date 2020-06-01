package com.farsitel.bazaar.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import com.farsitel.bazaar.auth.callback.CafeSingInCallback
import com.farsitel.bazaar.auth.connection.AuthConnection.Companion.getAuthConnection
import com.farsitel.bazaar.auth.model.CafeSignInAccount
import com.farsitel.bazaar.auth.model.CafeSignInOptions

class CafeSignIn {

    companion object {

        @JvmStatic
        fun getLastSignedInAccount(
            context: Context,
            owner: LifecycleOwner,
            callback: CafeSingInCallback
        ) {
            getAuthConnection(context).getLastAccountId(owner, callback)
        }

        @JvmStatic
        fun getLastSignedInAccountSync(
            context: Context,
            owner: LifecycleOwner
        ): CafeSignInAccount? {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                throw IllegalStateException("Can't call this method on UI thread.")
            }
            return getAuthConnection(context).getLastAccountIdSync(owner)
        }

        @JvmStatic
        fun getClient(activity: Activity, signInOption: CafeSignInOptions) =
            CafeSignInClient(signInOption, activity)

        @JvmStatic
        fun getSignedInAccountFromIntent(data: Intent?) =
            CafeSignInResult.getAccountFromIntent(data)
    }
}