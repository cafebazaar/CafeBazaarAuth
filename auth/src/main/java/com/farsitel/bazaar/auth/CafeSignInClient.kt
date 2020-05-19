package com.farsitel.bazaar.auth

import android.app.Activity
import com.farsitel.bazaar.auth.connection.AuthConnection
import com.farsitel.bazaar.auth.model.CafeSignInOptions

class CafeSignInClient {

    lateinit var signInOption: CafeSignInOptions
        internal set

    internal lateinit var activity: Activity

    fun getSignInIntentWithScope() {
        checkInitializing()

        AuthConnection.getAuthConnection(activity.applicationContext)
            .getSignInIntentWithScope(signInOption)
    }

    private fun checkInitializing() {
        if (!::signInOption.isInitialized || !::activity.isInitialized) {
            throw IllegalStateException(
                "You need to create CafeSignInClient with CafeSignIn#getClinet"
            )
        }
    }

}