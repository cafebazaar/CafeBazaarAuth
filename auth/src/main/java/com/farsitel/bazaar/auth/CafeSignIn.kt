package com.farsitel.bazaar.auth

import android.content.Context
import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import com.farsitel.bazaar.auth.callback.CafeSingInCallback
import com.farsitel.bazaar.auth.connection.getAuthConnection
import com.farsitel.bazaar.auth.model.CafeSignInAccount

object CafeSignIn {

    fun getLastSignedInAccount(
        context: Context,
        owner: LifecycleOwner,
        callback: CafeSingInCallback
    ) {
        getAuthConnection(context).getLastAccountId(owner, callback)
    }

    fun getLastSignedInAccountSync(context: Context, owner: LifecycleOwner): CafeSignInAccount? {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw IllegalStateException("Can't call this method in UI thread.")
        }
        return getAuthConnection(context).getLastAccountIdSync(owner)
    }
}