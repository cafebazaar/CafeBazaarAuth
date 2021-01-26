package com.farsitel.bazaar.storage.connection

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.lifecycle.LifecycleOwner
import com.farsitel.bazaar.BAZAAR_PACKAGE_NAME
import com.farsitel.bazaar.BazaarResponse
import com.farsitel.bazaar.storage.BazaarStorageAIDL
import com.farsitel.bazaar.storage.callback.BazaarStorageCallback
import com.farsitel.bazaar.thread.BackgroundThread
import com.farsitel.bazaar.thread.MainThread
import com.farsitel.bazaar.util.AbortableCountDownLatch
import com.farsitel.bazaar.util.ext.toBase64

internal class ServiceStorageConnection(
    private val context: Context,
    private val backgroundThread: BackgroundThread,
    private val mainThread: MainThread
) : StorageConnection(context), ServiceConnection {

    private var storageService: BazaarStorageAIDL? = null
    private var connectionThreadSecure: AbortableCountDownLatch? = null

    fun connect(): Boolean {
        return Intent(STORAGE_SERVICE_ACTION).apply {
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

    override fun getSavedData(owner: LifecycleOwner?, callback: BazaarStorageCallback) {
        withServiceInBackground(
            func = {
                val bundle = getSavedData(it)
                runOnMainThread {
                    callback.onDataReceived(getGetSavedDataResponse(bundle))
                }
            },
            onException = {
                runOnMainThread {
                    callback.onDataReceived(getGetSavedDataResponse(extras = null))
                }
            })
    }

    override fun getSavedDataSync(owner: LifecycleOwner?): BazaarResponse<ByteArray> {
        val bundle = withService(
            func = {
                getSavedData(it)
            },
            onException = {
                null
            }
        )

        return getGetSavedDataResponse(bundle)
    }

    private fun getSavedData(connection: BazaarStorageAIDL): Bundle? {
        return connection.getSavedData(context.packageName)
    }

    override fun saveData(
        owner: LifecycleOwner?,
        data: ByteArray,
        callback: BazaarStorageCallback
    ) {
        withServiceInBackground(
            func = {
                val bundle = saveData(it, data)
                val bazaarResponse = getSetDataResponse(bundle)
                runOnMainThread {
                    callback.onDataReceived(bazaarResponse)
                }
            },
            onException = {
                runOnMainThread {
                    callback.onDataReceived(getSetDataResponse(null))
                }
            }
        )
        saveDataSync(owner, data)
    }

    override fun saveDataSync(owner: LifecycleOwner?, data: ByteArray) {
        withService(
            func = {
                saveData(it, data)
            },
            onException = {
                null
            }
        )
    }

    private fun saveData(connection: BazaarStorageAIDL, data: ByteArray): Bundle? {
        return connection.saveData(context.packageName, data.toBase64())
    }

    override fun disconnect(context: Context) {
        context.unbindService(this)
        storageService = null
        connectionThreadSecure?.abort()

        backgroundThread.interrupt()
        super.disconnect(context)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        storageService = BazaarStorageAIDL.Stub.asInterface(service)
        connectionThreadSecure?.countDown()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        storageService = null
    }

    private fun <T> withService(
        func: (BazaarStorageAIDL) -> T,
        onException: () -> T
    ): T {
        return try {
            connectionThreadSecure?.await()
            if (storageService == null) {
                connect()
            }

            connectionThreadSecure?.await()
            func.invoke(requireNotNull(storageService))
        } catch (e: Exception) {
            onException.invoke()
        }
    }

    private fun <T> withServiceInBackground(
        func: (BazaarStorageAIDL) -> T,
        onException: () -> T
    ) {
        val runnable = {
            try {
                connectionThreadSecure?.await()
                if (storageService == null) {
                    connect()
                }

                connectionThreadSecure?.await()
                func.invoke(requireNotNull(storageService))
            } catch (e: Exception) {
                onException.invoke()
            }
            Unit
        }

        backgroundThread.execute(runnable)
    }

    private fun runOnMainThread(func: () -> Unit) {
        mainThread.post {
            func.invoke()
        }
    }

    companion object {
        private const val STORAGE_SERVICE_ACTION = "com.farsitel.bazaar.InAppStorage.BIND"
    }
}