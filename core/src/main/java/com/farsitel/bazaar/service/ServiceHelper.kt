package com.farsitel.bazaar.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.IInterface
import com.farsitel.bazaar.BAZAAR_PACKAGE_NAME
import com.farsitel.bazaar.thread.BackgroundThread
import com.farsitel.bazaar.util.AbortableCountDownLatch

internal class ServiceHelper<T : IInterface>(
    private val context: Context,
    private val backgroundThread: BackgroundThread,
    private val serviceIntentAction: String,
    private val onServiceConnectedFunc: (IBinder?) -> T
) : ServiceConnection {

    private var serviceConnection: T? = null
    private var connectionThreadSecure: AbortableCountDownLatch? = null

    fun connect(): Boolean {
        return Intent(serviceIntentAction).apply {
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
        serviceConnection = onServiceConnectedFunc(service)
        connectionThreadSecure?.countDown()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        serviceConnection = null
    }

    fun disconnect() {
        context.unbindService(this)
        serviceConnection = null
        connectionThreadSecure?.abort()
    }

    fun <E> withService(
        func: (T) -> E,
        onException: () -> E
    ): E {
        return runSafeOnService(func, onException)
    }

    fun <E> withServiceInBackground(
        func: (T) -> E,
        onException: () -> E
    ) {
        val runnable = {
            runSafeOnService(func, onException)
            Unit
        }

        backgroundThread.execute(runnable)
    }

    private fun <E> runSafeOnService(
        func: (T) -> E,
        onException: () -> E
    ) = try {
        connectionThreadSecure?.await()
        if (serviceConnection == null) {
            connect()
        }

        connectionThreadSecure?.await()
        func.invoke(requireNotNull(serviceConnection))
    } catch (e: Exception) {
        onException.invoke()
    }


}