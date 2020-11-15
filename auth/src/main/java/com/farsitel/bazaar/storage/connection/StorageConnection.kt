package com.farsitel.bazaar.storage.connection

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.farsitel.bazaar.BazaarResponse
import com.farsitel.bazaar.storage.callback.BazaarStorageCallback

internal abstract class StorageConnection(private val context: Context) {

    abstract fun getSavedData(owner: LifecycleOwner?, callback: BazaarStorageCallback)
    abstract fun getSavedDataSync(owner: LifecycleOwner?): BazaarResponse<ByteArray>?

    abstract fun saveData(owner: LifecycleOwner?, data: ByteArray, callback: BazaarStorageCallback)
    abstract fun saveDataSync(owner: LifecycleOwner?, data: ByteArray)

    companion object {
        const val PACKAGE_NAME_KEY = "packageName"

        private lateinit var storageConnection: StorageConnection
        private val lockObject = Object()

        fun getStorageConnection(context: Context): StorageConnection {
            if (!::storageConnection.isInitialized) {
                synchronized(lockObject) {
                    if (!::storageConnection.isInitialized) {
                        storageConnection = ReceiverStorageConnection(context)
                    }
                }
            }
            return storageConnection
        }
    }
}