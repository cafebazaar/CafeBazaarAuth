package com.farsitel.bazaar.storage.connection

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LifecycleOwner
import com.farsitel.bazaar.BAZAAR_PACKAGE_NAME
import com.farsitel.bazaar.BazaarResponse
import com.farsitel.bazaar.storage.BazaarStorageAIDL
import com.farsitel.bazaar.storage.callback.BazaarStorageCallback
import com.farsitel.bazaar.util.AbortableCountDownLatch

internal class ServiceStorageConnection(
    private val context: Context
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
        callback.onDataReceived(getSavedDataSync(owner))
    }

    override fun getSavedDataSync(owner: LifecycleOwner?): BazaarResponse<ByteArray>? {
        val bundle = withService(
            func = {
                it.getSavedData(context.packageName)
            },
            onException = {
                null
            }
        )

        return getGetSavedDataResponse(bundle)
    }

    override fun saveData(
        owner: LifecycleOwner?,
        data: ByteArray,
        callback: BazaarStorageCallback
    ) {
        saveDataSync(owner, data)
        callback.onDataReceived(
            BazaarResponse(
                isSuccessful = true,
                data = data
            )
        )
    }

    override fun saveDataSync(owner: LifecycleOwner?, data: ByteArray) {
        withService(
            func = {
                it.saveData(context.packageName, data)
            },
            onException = {
                null
            }
        )
    }

    override fun disconnect(context: Context) {
        context.unbindService(this)
        storageService = null
        connectionThreadSecure?.abort()

    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        storageService = BazaarStorageAIDL.Stub.asInterface(service)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        storageService = null
    }

    private fun <T> withService(func: (BazaarStorageAIDL) -> T, onException: () -> T): T {
        return try {
            if (storageService == null) {
                connect()
            }

            connectionThreadSecure?.await()
            func.invoke(requireNotNull(storageService))
        } catch (e: Exception) {
            onException.invoke()
        }
    }

    companion object {
        private const val STORAGE_SERVICE_ACTION = ""
    }
}