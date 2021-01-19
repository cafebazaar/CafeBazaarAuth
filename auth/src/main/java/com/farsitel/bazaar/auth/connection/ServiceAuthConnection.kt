package com.farsitel.bazaar.auth.connection

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LifecycleOwner
import com.farsitel.bazaar.BAZAAR_PACKAGE_NAME
import com.farsitel.bazaar.BazaarAuth
import com.farsitel.bazaar.BazaarResponse
import com.farsitel.bazaar.auth.callback.BazaarSignInCallback
import com.farsitel.bazaar.auth.model.BazaarSignInAccount
import com.farsitel.bazaar.util.AbortableCountDownLatch

internal class ServiceAuthConnection(
    private val context: Context
) : AuthConnection(context), ServiceConnection {

    private var authService: BazaarAuth? = null
    private var connectionThreadSecure: AbortableCountDownLatch? = null

    override fun getLastAccountId(
        owner: LifecycleOwner?,
        callback: BazaarSignInCallback
    ) {
        withService(
            func = { connection ->
                val bundle = connection.lastAccountId
                val response = getLastAccountResponse(bundle)
                callback.onAccountReceived(response)
            }, onException = {
                callback.onAccountReceived(getLastAccountResponse(extras = null))
            })
    }

    override fun getLastAccountIdSync(
        owner: LifecycleOwner?
    ): BazaarResponse<BazaarSignInAccount>? {
        val bundle = withService(
            func = { connection ->
                connection.lastAccountId
            }, onException = {
                null
            }
        )
        return getLastAccountResponse(bundle)
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
        authService = BazaarAuth.Stub.asInterface(service)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        authService = null
    }

    private fun <T> withService(func: (BazaarAuth) -> T, onException: () -> T): T {
        return try {
            if (authService == null) {
                connect()
            }

            connectionThreadSecure?.await()
            func.invoke(requireNotNull(authService))
        } catch (e: Exception) {
            onException.invoke()
        }
    }

    companion object {
        private const val AUTH_SERVICE_ACTION = ""
    }
}