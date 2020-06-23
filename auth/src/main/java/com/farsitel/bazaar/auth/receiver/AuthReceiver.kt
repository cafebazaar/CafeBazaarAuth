package com.farsitel.bazaar.auth.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import com.farsitel.bazaar.auth.util.SingleLiveEvent

class AuthReceiver : BroadcastReceiver() {

    companion object {
        private val _authReceiveObservable = SingleLiveEvent<Intent>()
        val authReceiveObservable: LiveData<Intent> = _authReceiveObservable
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        _authReceiveObservable.value = intent
    }
}