package com.farsitel.bazaar.auth.callback;

import com.farsitel.bazaar.BazaarResponse;
import com.farsitel.bazaar.auth.model.BazaarSignInAccount;

public interface BazaarSignInCallback {
    void onAccountReceived(BazaarResponse<BazaarSignInAccount> account);
}
