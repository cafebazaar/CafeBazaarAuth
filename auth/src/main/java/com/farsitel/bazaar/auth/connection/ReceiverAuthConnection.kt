package com.farsitel.bazaar.auth.connection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.farsitel.bazaar.auth.callback.CafeSingInCallback
import com.farsitel.bazaar.auth.model.CafeSignInAccount
import com.farsitel.bazaar.auth.receiver.AuthReceiver
import com.farsitel.bazaar.auth.util.AbortableCountDownLatch
import com.farsitel.bazaar.auth.util.InAppLoginLogger

internal class ReceiverAuthConnection(
    private val context: Context
) : AuthConnection(context) {

    private var cafeSingInCallback: CafeSingInCallback? = null

    private var getAccountIdLatch: AbortableCountDownLatch? = null
    private var cafeSignInAccount: CafeSignInAccount? = null

    override fun getLastAccountId(
        owner: LifecycleOwner,
        callback: CafeSingInCallback
    ) {
        cafeSingInCallback = callback
        sendBroadcastForLastAccountId(owner)
    }

    override fun getLastAccountIdSync(owner: LifecycleOwner): CafeSignInAccount? {
        sendBroadcastForLastAccountId(owner)
        getAccountIdLatch =
            AbortableCountDownLatch(1)
        getAccountIdLatch!!.await()
        return cafeSignInAccount
    }

    private fun sendBroadcastForLastAccountId(owner: LifecycleOwner) {
        listenOnIncomingBroadcastReceiver(owner)
        val intent = getNewIntentForBroadcast(GET_LAST_ACCOUNT_ACTION)
        context.sendBroadcast(intent)
    }

    private fun getNewIntentForBroadcast(action: String) = Intent().apply {
        val bazaarPackage = "com.farsitel.bazaar"
        setPackage(bazaarPackage)
        setAction(action)
        putExtra(PACKAGE_NAME_KEY, context.packageName)
    }

    private fun listenOnIncomingBroadcastReceiver(owner: LifecycleOwner) {
        AuthReceiver.authReceiveObserver.observe(owner, Observer { intent ->
            when (intent.action) {
                GET_LAST_ACCOUNT_ACTION_RESPONSE ->
                    handleGetLastAccountResponse(
                        intent.extras
                    )
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

        cafeSingInCallback?.onAccountReceived(account)
        getAccountIdLatch?.let {
            cafeSignInAccount = account
            it.countDown()
        }
    }

    companion object {
        private const val GET_LAST_ACCOUNT_ACTION = "com.farsitel.bazaar.lastAccount"

        private const val GET_LAST_ACCOUNT_ACTION_RESPONSE = "com.farsitel.bazaar.lastAccountRes"
    }

}