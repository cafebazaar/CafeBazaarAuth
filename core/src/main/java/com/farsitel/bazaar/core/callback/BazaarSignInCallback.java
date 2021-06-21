package com.farsitel.bazaar.core.callback;

import com.farsitel.bazaar.BazaarResponse;
import com.farsitel.bazaar.core.model.BazaarSignInAccount;

public interface BazaarSignInCallback {
    void onAccountReceived(BazaarResponse<BazaarSignInAccount> account);
}
