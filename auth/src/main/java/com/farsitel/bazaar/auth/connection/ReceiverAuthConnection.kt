package com.farsitel.bazaar.auth.connection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.farsitel.bazaar.BAZAAR_PACKAGE_NAME
import com.farsitel.bazaar.auth.callback.BazaarSignInCallback
import com.farsitel.bazaar.auth.model.BazaarSignInAccount
import com.farsitel.bazaar.auth.receiver.AuthReceiver
import com.farsitel.bazaar.util.AbortableCountDownLatch
import com.farsitel.bazaar.util.InAppLoginLogger

internal class ReceiverAuthConnection(
    private val context: Context
) : AuthConnection(context) {

    private var bazaarSignInCallback: BazaarSignInCallback? = null

    private var getAccountIdLatch: AbortableCountDownLatch? = null
    private var bazaarSignInAccount: BazaarSignInAccount? = null

    override fun getLastAccountId(
        owner: LifecycleOwner,
        callback: BazaarSignInCallback
    ) {
        bazaarSignInCallback = callback
        sendBroadcastForLastAccountId(owner)
    }

    override fun getLastAccountIdSync(owner: LifecycleOwner): BazaarSignInAccount? {
        sendBroadcastForLastAccountId(owner)
        getAccountIdLatch = AbortableCountDownLatch(1)
        getAccountIdLatch!!.await()
        return bazaarSignInAccount
    }

    private fun sendBroadcastForLastAccountId(owner: LifecycleOwner) {
        listenOnIncomingBroadcastReceiver(owner)
        val intent = getNewIntentForBroadcast(GET_LAST_ACCOUNT_ACTION)
        context.sendBroadcast(intent)
    }

    private fun getNewIntentForBroadcast(action: String) = Intent().apply {
        setPackage(BAZAAR_PACKAGE_NAME)
        setAction(action)
        putExtra(PACKAGE_NAME_KEY, context.packageName)
    }

    private fun listenOnIncomingBroadcastReceiver(owner: LifecycleOwner) {
        AuthReceiver.authReceiveObservable.observe(owner, Observer { intent ->
            when (intent.action) {
                GET_LAST_ACCOUNT_ACTION_RESPONSE -> {
                    handleGetLastAccountResponse(intent.extras)
                }
            }
        })
    }

    private fun handleGetLastAccountResponse(extras: Bundle?) {
        if (extras == null) {
            return
        }

        val account = if (AuthResponseHandler.isSuccessful(extras)) {
            AuthResponseHandler.getAccountByBundle(extras)
        } else {
            InAppLoginLogger.logError(AuthResponseHandler.getErrorMessage(extras))
            null
        }

        bazaarSignInCallback?.onAccountReceived(account)
        getAccountIdLatch?.let {
            bazaarSignInAccount = account
            it.countDown()
        }
    }

    companion object {
        private const val GET_LAST_ACCOUNT_ACTION = "$BAZAAR_PACKAGE_NAME.lastAccount"
        private const val GET_LAST_ACCOUNT_ACTION_RESPONSE = "$BAZAAR_PACKAGE_NAME.lastAccountRes"
    }

}