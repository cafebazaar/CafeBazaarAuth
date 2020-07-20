package com.farsitel.bazaar.storage.connection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.farsitel.bazaar.BAZAAR_PACKAGE_NAME
import com.farsitel.bazaar.storage.callback.BazaarStorageCallback
import com.farsitel.bazaar.storage.receiver.StorageReceiver
import com.farsitel.bazaar.util.AbortableCountDownLatch
import com.farsitel.bazaar.util.InAppLoginLogger

internal class ReceiverStorageConnection(
    private val context: Context
) : StorageConnection(context) {

    private var bazaarSetStorageCallback: BazaarStorageCallback? = null
    private var bazaarGetStorageCallback: BazaarStorageCallback? = null

    private var getStorageLatch: AbortableCountDownLatch? = null
    private var setStorageLatch: AbortableCountDownLatch? = null

    private var bazaarStorage: String? = null

    override fun getSavedData(owner: LifecycleOwner?, callback: BazaarStorageCallback) {
        bazaarGetStorageCallback = callback
        sendBroadcastForGetSavedData(owner)
    }

    override fun getSavedDataSync(owner: LifecycleOwner?): String? {
        sendBroadcastForGetSavedData(owner)
        getStorageLatch = AbortableCountDownLatch(1)
        getStorageLatch!!.await()
        return bazaarStorage
    }


    override fun savedData(owner: LifecycleOwner?, data: String, callback: BazaarStorageCallback) {
        bazaarGetStorageCallback = callback
        sendBroadcastForSaveData(owner)
    }

    override fun savedDataSync(owner: LifecycleOwner?, data: String) {
        sendBroadcastForSaveData(owner)
        setStorageLatch = AbortableCountDownLatch(1)
        setStorageLatch!!.await()
    }

    private fun sendBroadcastForGetSavedData(owner: LifecycleOwner?) {
        listenOnIncomingBroadcastReceiver(owner)
        val intent = getNewIntentForBroadcast(ACTION_STORAGE_GET_DATA)
        context.sendBroadcast(intent)
    }

    private fun sendBroadcastForSaveData(owner: LifecycleOwner?) {
        listenOnIncomingBroadcastReceiver(owner)
        val intent = getNewIntentForBroadcast(ACTION_STORAGE_SET_DATA)
        context.sendBroadcast(intent)
    }


    private fun listenOnIncomingBroadcastReceiver(owner: LifecycleOwner?) {
        val observer = Observer<Intent> { intent ->
            when (intent.action) {
                ACTION_STORAGE_GET_DATA_RESPONSE -> {
                    handleGetSavedDataResponse(intent.extras)
                }
                ACTION_STORAGE_SET_DATA_RESPONSE -> {
                    handleSetDataResponse(intent.extras)
                }
                ACTION_STORAGE_SET_DATA,
                ACTION_STORAGE_GET_DATA -> {
                    context.sendBroadcast(intent)
                }
            }
        }

        if (owner == null) {
            StorageReceiver.storageReceiveObservable.observeForever(observer)
        } else {
            StorageReceiver.storageReceiveObservable.observe(owner, observer)
        }
    }

    private fun getNewIntentForBroadcast(action: String, bundle: Bundle? = null) = Intent().apply {
        setPackage(BAZAAR_PACKAGE_NAME)
        setAction(action)
        putExtra(PACKAGE_NAME_KEY, context.packageName)
        bundle?.let { notNullBundle ->
            putExtras(notNullBundle)
        }
    }

    private fun handleGetSavedDataResponse(extras: Bundle?) {
        if (extras == null) {
            return
        }

        val savedData = if (StorageResponseHandler.isSuccessful(extras)) {
            StorageResponseHandler.getSavedData(extras)
        } else {
            InAppLoginLogger.logError(StorageResponseHandler.getErrorMessage(extras))
            null
        }

        bazaarGetStorageCallback?.onDataReceived(savedData)
        getStorageLatch?.let {
            bazaarStorage = savedData
            it.countDown()
        }
    }

    private fun handleSetDataResponse(extras: Bundle?) {
        if (extras == null) {
            return
        }

        val savedData = if (StorageResponseHandler.isSuccessful(extras)) {
            StorageResponseHandler.getSavedData(extras)
        } else {
            InAppLoginLogger.logError(StorageResponseHandler.getErrorMessage(extras))
            null
        }

        bazaarSetStorageCallback?.onDataReceived(savedData)
        setStorageLatch?.countDown()
    }

    companion object {
        private const val ACTION_STORAGE_GET_DATA = "$BAZAAR_PACKAGE_NAME.getInAppData"
        private const val ACTION_STORAGE_GET_DATA_RESPONSE = "$BAZAAR_PACKAGE_NAME.getInAppDataRes"

        private const val ACTION_STORAGE_SET_DATA = "$BAZAAR_PACKAGE_NAME.setInAppData"
        private const val ACTION_STORAGE_SET_DATA_RESPONSE = "$BAZAAR_PACKAGE_NAME.setInAppDataRes"
    }
}