package com.farsitel.bazaar.storage.connection

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.farsitel.bazaar.BazaarResponse
import com.farsitel.bazaar.storage.callback.BazaarStorageCallback
import com.farsitel.bazaar.thread.BackgroundThread
import com.farsitel.bazaar.thread.MainThread
import com.farsitel.bazaar.util.InAppLoginLogger
import com.farsitel.bazaar.util.fromBase64

internal abstract class StorageConnection(private val context: Context) {

    abstract fun getSavedData(owner: LifecycleOwner?, callback: BazaarStorageCallback)
    abstract fun getSavedDataSync(owner: LifecycleOwner?): BazaarResponse<ByteArray>?

    abstract fun saveData(owner: LifecycleOwner?, data: ByteArray, callback: BazaarStorageCallback)
    abstract fun saveDataSync(owner: LifecycleOwner?, data: ByteArray)

    open fun disconnect(context: Context) {
        storageConnection = null
    }

    fun getGetSavedDataResponse(extras: Bundle?): BazaarResponse<ByteArray> {
        return if (StorageResponseHandler.isSuccessful(extras)) {
            val data = StorageResponseHandler.getSavedData(extras)
            val payload = data?.fromBase64()

            BazaarResponse(isSuccessful = true, data = payload)
        } else {
            val errorResponse = StorageResponseHandler.getErrorResponse(extras)
            InAppLoginLogger.logError(errorResponse?.errorMessage)

            BazaarResponse(isSuccessful = true, errorResponse = errorResponse)
        }
    }

    fun getSetDataResponse(extras: Bundle?): BazaarResponse<ByteArray> {
        return if (StorageResponseHandler.isSuccessful(extras)) {
            val data = StorageResponseHandler.getSavedData(extras)
            val payload = data?.fromBase64()

            BazaarResponse(isSuccessful = true, data = payload)
        } else {
            val errorResponse = StorageResponseHandler.getErrorResponse(extras)
            InAppLoginLogger.logError(errorResponse?.errorMessage)

            BazaarResponse(isSuccessful = true, errorResponse = errorResponse)
        }
    }

    companion object {
        const val PACKAGE_NAME_KEY = "packageName"

        private var storageConnection: StorageConnection? = null
        private val lockObject = Object()

        fun getStorageConnection(context: Context): StorageConnection {
            if (storageConnection == null) {
                synchronized(lockObject) {
                    if (storageConnection == null) {
                        initializeStorageConnection(context)
                    }
                }
            }
            return requireNotNull(storageConnection)
        }

        private fun initializeStorageConnection(context: Context) {
            val serviceConnection = ServiceStorageConnection(
                context,
                BackgroundThread(),
                MainThread()
            )
            val canConnectWithService = serviceConnection.connect()

            storageConnection = if (canConnectWithService) {
                serviceConnection
            } else {
                ReceiverStorageConnection(context)
            }
        }
    }
}