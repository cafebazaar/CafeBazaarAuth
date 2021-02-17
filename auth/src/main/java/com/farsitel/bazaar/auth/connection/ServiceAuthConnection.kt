package com.farsitel.bazaar.auth.connection

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LifecycleOwner
import com.farsitel.bazaar.BAZAAR_PACKAGE_NAME
import com.farsitel.bazaar.BazaarResponse
import com.farsitel.bazaar.auth.InAppBazaarAuth
import com.farsitel.bazaar.auth.callback.BazaarSignInCallback
import com.farsitel.bazaar.auth.model.BazaarSignInAccount
import com.farsitel.bazaar.thread.BackgroundThread
import com.farsitel.bazaar.util.AbortableCountDownLatch

internal class ServiceAuthConnection(
    private val context: Context,
    private val backgroundThread: BackgroundThread
) : AuthConnection(context), ServiceConnection {

    private var authService: InAppBazaarAuth? = null
    private var connectionThreadSecure: AbortableCountDownLatch? = null

    override fun getLastAccountId(
        owner: LifecycleOwner?,
        callback: BazaarSignInCallback
    ) {
        withServiceInBackground(
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
        return withService(
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
        context.unbindService(this)

        authService = null
        connectionThreadSecure?.abort()

        backgroundThread.interrupt()
        super.disconnect()
    }

    fun connect(): Boolean {
        return Intent(AUTH_SERVICE_ACTION).apply {
            `package` = BAZAAR_PACKAGE_NAME
        }.takeIf {
            context.packageManager.queryIntentServices(it, 0).isNullOrEmpty().not()
        }?.let {
            try {
                val connect = context.bindService(it, this, Context.BIND_AUTO_CREATE)
                if (connect) {
                    connectionThreadSecure = AbortableCountDownLatch(1)
                }
                connect
            } catch (e: SecurityException) {
                false
            }
        } ?: false
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        authService = InAppBazaarAuth.Stub.asInterface(service)
        connectionThreadSecure?.countDown()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        authService = null
    }

    private fun <T> withService(
        func: (InAppBazaarAuth) -> T,
        onException: () -> T
    ): T {
        return runSafeOnService(func, onException)
    }

    private fun <T> withServiceInBackground(
        func: (InAppBazaarAuth) -> T,
        onException: () -> T
    ) {
        val runnable = {
            runSafeOnService(func, onException)
            Unit
        }

        backgroundThread.execute(runnable)
    }

    private fun <T> runSafeOnService(
        func: (InAppBazaarAuth) -> T,
        onException: () -> T
    ) = try {
        connectionThreadSecure?.await()
        if (authService == null) {
            connect()
        }

        connectionThreadSecure?.await()
        func.invoke(requireNotNull(authService))
    } catch (e: Exception) {
        onException.invoke()
    }

    companion object {
        private const val AUTH_SERVICE_ACTION = "com.farsitel.bazaar.InAppLogin.BIND"
    }
}