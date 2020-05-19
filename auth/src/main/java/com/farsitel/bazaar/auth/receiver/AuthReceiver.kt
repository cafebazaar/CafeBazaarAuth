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
        val sendIntent = Intent().apply {
            action = intent!!.action

            val bundle = intent.extras
            if (bundle != null) {
                putExtras(bundle)
            }
        }

        _authReceiveObserver.value = sendIntent
    }
}