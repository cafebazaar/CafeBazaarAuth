package com.farsitel.bazaar.storage.connection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.farsitel.bazaar.BAZAAR_PACKAGE_NAME
import com.farsitel.bazaar.BazaarResponse
import com.farsitel.bazaar.storage.callback.BazaarStorageCallback
import com.farsitel.bazaar.storage.receiver.StorageReceiver
import com.farsitel.bazaar.util.AbortableCountDownLatch
import com.farsitel.bazaar.util.InAppLoginLogger
import com.farsitel.bazaar.util.ext.toBase64
import com.farsitel.bazaar.util.fromBase64

internal class ReceiverStorageConnection(
    private val context: Context
) : StorageConnection(context) {

    private var bazaarSetStorageCallback: BazaarStorageCallback? = null
    private var bazaarGetStorageCallback: BazaarStorageCallback? = null

    private var getStorageLatch: AbortableCountDownLatch? = null
    private var setStorageLatch: AbortableCountDownLatch? = null

    private var bazaarResponse: BazaarResponse<ByteArray>? = null

    private val observer = Observer<Intent> { intent ->
        when (intent.action) {
            ACTION_STORAGE_GET_DATA_RESPONSE -> {
                handleGetSavedDataResponse(intent.extras)
            }
            ACTION_STORAGE_SET_DATA_RESPONSE -> {
                handleSetDataResponse(intent.extras)
            }
            ACTION_STORAGE_SET_DATA,
            ACTION_STORAGE_GET_DATA -> {
                context.sendBroadcast(
                    getNewIntentForBroadcast(
                        requireNotNull(intent.action),
                        intent.extras
                    )
                )
            }
        }
    }

    override fun getSavedData(owner: LifecycleOwner?, callback: BazaarStorageCallback) {
        bazaarGetStorageCallback = callback
        sendBroadcastForGetSavedData(owner)
    }

    override fun getSavedDataSync(owner: LifecycleOwner?): BazaarResponse<ByteArray>? {
        sendBroadcastForGetSavedData(owner)
        getStorageLatch = AbortableCountDownLatch(1)
        getStorageLatch!!.await()
        return bazaarResponse
    }

    override fun saveData(
        owner: LifecycleOwner?,
        data: ByteArray,
        callback: BazaarStorageCallback
    ) {
        bazaarSetStorageCallback = callback
        sendBroadcastForSaveData(owner, data)
    }

    override fun saveDataSync(owner: LifecycleOwner?, data: ByteArray) {
        sendBroadcastForSaveData(owner, data)
        setStorageLatch = AbortableCountDownLatch(1)
        setStorageLatch!!.await()
    }

    private fun sendBroadcastForGetSavedData(owner: LifecycleOwner?) {
        listenOnIncomingBroadcastReceiver(owner)
        val intent = getNewIntentForBroadcast(ACTION_STORAGE_GET_DATA)
        context.sendBroadcast(intent)
    }

    private fun sendBroadcastForSaveData(owner: LifecycleOwner?, data: ByteArray) {
        listenOnIncomingBroadcastReceiver(owner)
        val bundle = Bundle().apply {
            putString(KEY_PAYLOAD, data.toBase64())
        }
        val intent = getNewIntentForBroadcast(ACTION_STORAGE_SET_DATA, bundle)
        context.sendBroadcast(intent)
    }

    private fun listenOnIncomingBroadcastReceiver(owner: LifecycleOwner?) {
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

        val bazaarResponse = if (StorageResponseHandler.isSuccessful(extras)) {
            val data = StorageResponseHandler.getSavedData(extras)
            val payload = data?.fromBase64()

            BazaarResponse(isSuccessful = true, data = payload)
        } else {
            val errorResponse = StorageResponseHandler.getErrorMessage(extras)
            InAppLoginLogger.logError(errorResponse?.errorMessage)

            BazaarResponse(isSuccessful = true, errorResponse = errorResponse)
        }


        bazaarGetStorageCallback?.onDataReceived(bazaarResponse)
        getStorageLatch?.let {
            this@ReceiverStorageConnection.bazaarResponse = bazaarResponse
            it.countDown()
        }
    }

    private fun handleSetDataResponse(extras: Bundle?) {
        if (extras == null) {
            return
        }

        val bazaarResponse = if (StorageResponseHandler.isSuccessful(extras)) {
            val data = StorageResponseHandler.getSavedData(extras)
            val payload = data?.fromBase64()

            BazaarResponse(isSuccessful = true, data = payload)
        } else {
            val errorResponse = StorageResponseHandler.getErrorMessage(extras)
            InAppLoginLogger.logError(errorResponse?.errorMessage)

            BazaarResponse(isSuccessful = true, errorResponse = errorResponse)
        }

        bazaarSetStorageCallback?.onDataReceived(bazaarResponse)
        setStorageLatch?.countDown()
    }

    companion object {
        private const val ACTION_STORAGE_GET_DATA = "$BAZAAR_PACKAGE_NAME.getInAppData"
        private const val ACTION_STORAGE_GET_DATA_RESPONSE = "$BAZAAR_PACKAGE_NAME.getInAppDataRes"

        private const val ACTION_STORAGE_SET_DATA = "$BAZAAR_PACKAGE_NAME.setInAppData"
        private const val ACTION_STORAGE_SET_DATA_RESPONSE = "$BAZAAR_PACKAGE_NAME.setInAppDataRes"

        private const val KEY_PAYLOAD = "payload"
    }
}