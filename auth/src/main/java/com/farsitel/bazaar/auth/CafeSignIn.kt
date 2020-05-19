package com.farsitel.bazaar.auth

import android.app.Activity
import android.content.Context
import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import com.farsitel.bazaar.auth.callback.CafeSingInCallback
import com.farsitel.bazaar.auth.connection.AuthConnection.Companion.getAuthConnection
import com.farsitel.bazaar.auth.model.CafeSignInAccount
import com.farsitel.bazaar.auth.model.CafeSignInOptions

class CafeSignIn {

    companion object {
        fun getLastSignedInAccount(
            context: Context,
            owner: LifecycleOwner,
            callback: CafeSingInCallback
        ) {
            getAuthConnection(context).getLastAccountId(owner, callback)
        }

        fun getLastSignedInAccountSync(
            context: Context,
            owner: LifecycleOwner
        ): CafeSignInAccount? {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                throw IllegalStateException("Can't call this method in UI thread.")
            }
            return getAuthConnection(context).getLastAccountIdSync(owner)
        }

        @JvmStatic
        fun getClient(activity: Activity, signInOption: CafeSignInOptions) =
            CafeSignInClient().apply {
                this.signInOption = signInOption
                this.activity = activity
            }
    }
}