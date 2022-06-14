package com.farsitel.bazaar.storage.connection

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.farsitel.bazaar.BazaarResponse
import com.farsitel.bazaar.service.ServiceHelper
import com.farsitel.bazaar.storage.InAppBazaarStorage
import com.farsitel.bazaar.storage.callback.BazaarStorageCallback
import com.farsitel.bazaar.thread.BackgroundThread
import com.farsitel.bazaar.thread.MainThread
import com.farsitel.bazaar.util.ext.toBase64

internal class ServiceStorageConnection(
    private val context: Context,
    private val backgroundThread: BackgroundThread,
    private val mainThread: MainThread
) : StorageConnection(context) {

    private val serviceHelper = ServiceHelper<InAppBazaarStorage>(
        context,
        backgroundThread,
        STORAGE_SERVICE_ACTION
    ) { binder ->
        InAppBazaarStorage.Stub.asInterface(binder)
    }

    fun connect(): Boolean {
        return serviceHelper.connect()
    }

    override fun getSavedData(owner: LifecycleOwner?, callback: BazaarStorageCallback) {
        serviceHelper.withServiceInBackground(
            func = {
                val bundle = getSavedData(it)
                runOnMainThread {
                    callback.onDataReceived(retrieveGetSavedDataResponse(bundle))
                }
            },
            onException = {
                runOnMainThread {
                    callback.onDataReceived(retrieveGetSavedDataResponse(extras = null))
                }
            })
    }

    override fun getSavedDataSync(owner: LifecycleOwner?): BazaarResponse<ByteArray> {
        val bundle = serviceHelper.withService(
            func = {
                getSavedData(it)
            },
            onException = {
                null
            }
        )

        return retrieveGetSavedDataResponse(bundle)
    }

    private fun getSavedData(connection: InAppBazaarStorage): Bundle? {
        return connection.getSavedData(context.packageName)
    }

    override fun saveData(
        owner: LifecycleOwner?,
        data: ByteArray,
        callback: BazaarStorageCallback
    ) {
        serviceHelper.withServiceInBackground(
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
    }

    override fun saveDataSync(owner: LifecycleOwner?, data: ByteArray) {
        serviceHelper.withService(
            func = {
                saveData(it, data)
            },
            onException = {
                null
            }
        )
    }

    private fun saveData(connection: InAppBazaarStorage, data: ByteArray): Bundle? {
        return connection.saveData(context.packageName, data.toBase64())
    }

    override fun disconnect() {
        serviceHelper.disconnect()
        backgroundThread.interrupt()
        super.disconnect()
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