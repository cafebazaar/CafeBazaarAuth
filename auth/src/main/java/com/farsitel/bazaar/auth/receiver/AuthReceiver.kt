package com.farsitel.bazaar.auth.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import com.farsitel.bazaar.auth.util.SingleLiveEvent

class AuthReceiver : BroadcastReceiver() {

    companion object {
        private val _authReceiveObserver = SingleLiveEvent<Intent>()
        val authReceiveObserver: LiveData<Intent> = _authReceiveObserver
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        _authReceiveObserver.value = intent
    }
}