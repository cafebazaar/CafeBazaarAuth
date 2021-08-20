package com.farsitel.bazaar.core.connection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.farsitel.bazaar.BAZAAR_PACKAGE_NAME
import com.farsitel.bazaar.BazaarResponse
import com.farsitel.bazaar.core.callback.BazaarSignInCallback
import com.farsitel.bazaar.core.model.BazaarSignInAccount
import com.farsitel.bazaar.core.receiver.AuthReceiver
import com.farsitel.bazaar.thread.MainThread
import com.farsitel.bazaar.thread.ThreadHelper.changeToMainThreadIfNeeded
import com.farsitel.bazaar.util.AbortableCountDownLatch

internal class ReceiverAuthConnection(
    private val context: Context,
    private val mainThread: MainThread
) : AuthConnection() {

    private var bazaarSignInCallback: BazaarSignInCallback? = null

    private var getAccountIdLatch: AbortableCountDownLatch? = null
    private var bazaarSignInAccountResponse: BazaarResponse<BazaarSignInAccount>? = null

    private val observer = Observer<Intent> { intent ->
        when (intent.action) {
            GET_LAST_ACCOUNT_ACTION_RESPONSE -> {
                handleGetLastAccountResponse(intent.extras)
            }
        }
    }

    override fun getLastAccountId(
        owner: LifecycleOwner?,
        callback: BazaarSignInCallback,
        mainThread: MainThread
    ) {
        bazaarSignInCallback = callback
        sendBroadcastForLastAccountId(owner)
    }

    override fun getLastAccountIdSync(
        owner: LifecycleOwner?
    ): BazaarResponse<BazaarSignInAccount>? {
        sendBroadcastForLastAccountId(owner)
        getAccountIdLatch = AbortableCountDownLatch(1)
        getAccountIdLatch!!.await()
        return bazaarSignInAccountResponse
    }

    override fun disconnect() {
        bazaarSignInCallback = null
        bazaarSignInAccountResponse = null

        getAccountIdLatch?.abort()
        super.disconnect()
    }

    private fun sendBroadcastForLastAccountId(owner: LifecycleOwner?) {
        listenOnIncomingBroadcastReceiver(owner)
        val intent = getNewIntentForBroadcast(GET_LAST_ACCOUNT_ACTION)
        context.sendBroadcast(intent)
    }

    private fun getNewIntentForBroadcast(action: String) = Intent().apply {
        setPackage(BAZAAR_PACKAGE_NAME)
        setAction(action)
        putExtra(PACKAGE_NAME_KEY, context.packageName)
    }

    private fun listenOnIncomingBroadcastReceiver(owner: LifecycleOwner?) {

        val task = {
            if (owner == null) {
                AuthReceiver.authReceiveObservable.observeForever(observer)
            } else {
                AuthReceiver.authReceiveObservable.observe(owner, observer)
            }
        }

        changeToMainThreadIfNeeded(mainThread, task)
    }

    private fun handleGetLastAccountResponse(extras: Bundle?) {
        val response = getLastAccountResponse(extras)
        mainThread.post {
            bazaarSignInCallback?.onAccountReceived(response)
        }
        getAccountIdLatch?.let {
            bazaarSignInAccountResponse = response
            it.countDown()
        }
    }

    companion object {
        private const val GET_LAST_ACCOUNT_ACTION = "$BAZAAR_PACKAGE_NAME.lastAccount"
        private const val GET_LAST_ACCOUNT_ACTION_RESPONSE = "$BAZAAR_PACKAGE_NAME.lastAccountRes"
    }

}