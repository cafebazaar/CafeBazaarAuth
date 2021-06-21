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
import com.farsitel.bazaar.thread.MainThread
import com.farsitel.bazaar.thread.ThreadHelper
import com.farsitel.bazaar.util.AbortableCountDownLatch
import com.farsitel.bazaar.util.ext.toBase64

internal class ReceiverStorageConnection(
    private val context: Context,
    private val mainThread: MainThread
) : StorageConnection(context) {

    private var bazaarSetStorageCallback: BazaarStorageCallback? = null
    private var bazaarGetStorageCallback: BazaarStorageCallback? = null

    private var getStorageLatch: AbortableCountDownLatch? = null
    private var setStorageLatch: AbortableCountDownLatch? = null

    private var bazaarResponse: BazaarResponse<ByteArray>? = null

    private var sendSetDataBroadcast = false
    private var sendGetDataBroadcast = false

    private val observer = Observer<Intent> { intent ->
        when (intent.action) {
            ACTION_STORAGE_GET_DATA_RESPONSE -> {
                handleGetSavedDataResponse(intent.extras)
            }
            ACTION_STORAGE_SET_DATA_RESPONSE -> {
                handleSetDataResponse(intent.extras)
            }
            ACTION_STORAGE_SET_DATA -> {
                if (sendSetDataBroadcast) {
                    sendSetDataBroadcast = false
                    context.sendBroadcast(
                        getNewIntentForBroadcast(
                            requireNotNull(intent.action),
                            intent.extras
                        )
                    )
                }
            }
            ACTION_STORAGE_GET_DATA -> {
                if (sendGetDataBroadcast) {
                    sendGetDataBroadcast = false
                    context.sendBroadcast(
                        getNewIntentForBroadcast(
                            requireNotNull(intent.action),
                            intent.extras
                        )
                    )
                }
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

    override fun disconnect() {
        bazaarSetStorageCallback = null
        bazaarGetStorageCallback = null

        getStorageLatch?.abort()
        setStorageLatch?.abort()
        super.disconnect()
    }

    private fun sendBroadcastForGetSavedData(owner: LifecycleOwner?) {
        listenOnIncomingBroadcastReceiver(owner)
        val intent = getNewIntentForBroadcast(ACTION_STORAGE_GET_DATA)
        context.sendBroadcast(intent)
        sendGetDataBroadcast = true
    }

    private fun sendBroadcastForSaveData(owner: LifecycleOwner?, data: ByteArray) {
        listenOnIncomingBroadcastReceiver(owner)
        val bundle = Bundle().apply {
            putString(KEY_PAYLOAD, data.toBase64())
        }
        val intent = getNewIntentForBroadcast(ACTION_STORAGE_SET_DATA, bundle)
        context.sendBroadcast(intent)
        sendSetDataBroadcast = true
    }

    private fun listenOnIncomingBroadcastReceiver(owner: LifecycleOwner?) {
        val task = {
            if (owner == null) {
                StorageReceiver.storageReceiveObservable.observeForever(observer)
            } else {
                StorageReceiver.storageReceiveObservable.observe(owner, observer)
            }
        }

        ThreadHelper.changeToMainThreadIfNeeded(mainThread, task)
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
        val bazaarResponse = retrieveGetSavedDataResponse(extras)

        bazaarGetStorageCallback?.onDataReceived(bazaarResponse)
        getStorageLatch?.let {
            this@ReceiverStorageConnection.bazaarResponse = bazaarResponse
            it.countDown()
        }
    }

    private fun handleSetDataResponse(extras: Bundle?) {
        val bazaarResponse = getSetDataResponse(extras)

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