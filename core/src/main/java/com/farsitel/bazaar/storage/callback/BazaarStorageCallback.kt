package com.farsitel.bazaar.storage.callback

import com.farsitel.bazaar.BazaarResponse

fun interface BazaarStorageCallback {
    fun onDataReceived(response: BazaarResponse<ByteArray>?)
}