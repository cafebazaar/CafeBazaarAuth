package com.farsitel.bazaar.storage

import android.content.Context
import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import com.farsitel.bazaar.BazaarResponse
import com.farsitel.bazaar.storage.callback.BazaarStorageCallback
import com.farsitel.bazaar.storage.connection.StorageConnection

object BazaarStorage {

    @JvmStatic
    fun getSavedData(
        context: Context,
        owner: LifecycleOwner?,
        callback: BazaarStorageCallback
    ) {
        StorageConnection.getStorageConnection(context).getSavedData(owner, callback)
    }

    @JvmStatic
    fun getSavedDataSync(
        context: Context,
        owner: LifecycleOwner?
    ): BazaarResponse<ByteArray>? {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw IllegalStateException("Can't call this method on UI thread.")
        }
        return StorageConnection.getStorageConnection(context).getSavedDataSync(owner)
    }

    @JvmStatic
    fun saveData(
        context: Context,
        owner: LifecycleOwner?,
        data: ByteArray,
        callback: BazaarStorageCallback
    ) {
        StorageConnection.getStorageConnection(context).saveData(owner, data, callback)
    }

    @JvmStatic
    fun saveDataSync(
        context: Context,
        owner: LifecycleOwner?,
        data: ByteArray
    ) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw IllegalStateException("Can't call this method on UI thread.")
        }

        StorageConnection.getStorageConnection(context).saveDataSync(owner, data)
    }

    @JvmStatic
    fun disconnect(context: Context) {
        StorageConnection.getStorageConnection(context).disconnect(context)
    }
}