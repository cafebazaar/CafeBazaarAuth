package com.farsitel.bazaar.storage.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import com.farsitel.bazaar.util.SingleLiveEvent

class StorageReceiver : BroadcastReceiver() {

    companion object {
        private val _storageReceiveObservable =
            SingleLiveEvent<Intent>()
        val storageReceiveObservable: LiveData<Intent> = _storageReceiveObservable
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        _storageReceiveObservable.value = intent
    }
}