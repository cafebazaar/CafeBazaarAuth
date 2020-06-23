package com.farsitel.bazaar.auth.callback;

import androidx.annotation.Nullable;

import com.farsitel.bazaar.auth.model.BazaarSignInAccount;

public interface BazaarSignInCallback {
    void onAccountReceived(@Nullable BazaarSignInAccount account);
}
