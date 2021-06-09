package com.farsitel.bazaar.core.connection

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.farsitel.bazaar.BazaarResponse
import com.farsitel.bazaar.auth.InAppBazaarAuth
import com.farsitel.bazaar.core.callback.BazaarSignInCallback
import com.farsitel.bazaar.core.model.BazaarSignInAccount
import com.farsitel.bazaar.service.ServiceHelper
import com.farsitel.bazaar.thread.BackgroundThread

internal class ServiceAuthConnection(
    private val context: Context,
    private val backgroundThread: BackgroundThread
) : AuthConnection(context) {

    private val serviceHelper = ServiceHelper<InAppBazaarAuth>(
        context,
        backgroundThread,
        AUTH_SERVICE_ACTION
    ) { binder ->
        InAppBazaarAuth.Stub.asInterface(binder)
    }

    override fun getLastAccountId(
        owner: LifecycleOwner?,
        callback: BazaarSignInCallback
    ) {
        serviceHelper.withServiceInBackground(
            func = { connection ->
                val response = getLastAccountBundleFromService(connection)
                callback.onAccountReceived(response)
            }, onException = {
                callback.onAccountReceived(getLastAccountResponse(extras = null))
            })
    }

    override fun getLastAccountIdSync(
        owner: LifecycleOwner?
    ): BazaarResponse<BazaarSignInAccount> {
        return serviceHelper.withService(
            func = { connection ->
                getLastAccountBundleFromService(connection)
            }, onException = {
                getLastAccountResponse(extras = null)
            }
        )
    }

    private fun getLastAccountBundleFromService(
        connection: InAppBazaarAuth
    ): BazaarResponse<BazaarSignInAccount> {
        val bundle = connection.getLastAccountId(context.packageName)
        return getLastAccountResponse(bundle)
    }

    override fun disconnect() {
        serviceHelper.disconnect()
        backgroundThread.interrupt()
        super.disconnect()
    }

    fun connect(): Boolean {
        return serviceHelper.connect()
    }

    companion object {
        private const val AUTH_SERVICE_ACTION = "com.farsitel.bazaar.InAppLogin.BIND"
    }
}