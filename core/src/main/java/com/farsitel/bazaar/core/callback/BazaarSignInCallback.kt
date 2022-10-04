package com.farsitel.bazaar.core.callback

import com.farsitel.bazaar.BazaarResponse
import com.farsitel.bazaar.core.model.BazaarSignInAccount

fun interface BazaarSignInCallback {
    fun onAccountReceived(account: BazaarResponse<BazaarSignInAccount>)
}