package com.farsitel.bazaar.auth.connection

import android.content.Context
import android.content.Intent
import com.farsitel.bazaar.auth.CafeSingInCallback
import com.farsitel.bazaar.auth.model.AbortableCountDownLatch
import com.farsitel.bazaar.auth.model.CafeSignInAccount

class ReceiverAuthConnection(
    private val context: Context
) : AuthConnection(context) {

    private var cafeSingInCallback: CafeSingInCallback? = null

    private var getAccountIdLatch: AbortableCountDownLatch? = null
    private var cafeSignInAccount: CafeSignInAccount? = null

    override fun getLastAccountId(
        callback: CafeSingInCallback
    ) {

        cafeSingInCallback = callback
        sendBroadcastForLastAccountId()
    }

    override fun getLastAccountIdSync(): CafeSignInAccount? {
        sendBroadcastForLastAccountId()
        getAccountIdLatch = AbortableCountDownLatch(1).apply {
            await()
        }

        return cafeSignInAccount
    }

    private fun sendBroadcastForLastAccountId() {
        val intent = getNewIntentForBroadcast(GET_LAST_ACCOUNT_ACTION)
        context.sendBroadcast(intent)
    }

    private fun getNewIntentForBroadcast(action: String) = Intent().apply {
        val bazaarPackage = "com.farsitel.bazaar"
        setPackage(bazaarPackage)
        setAction(action)
        putExtra(PACKAGE_NAME_KEY, context.packageName)
    }

    companion object {

        private const val PACKAGE_NAME_KEY = "packageName"

        private const val GET_LAST_ACCOUNT_ACTION = "lastAccount"
    }

}