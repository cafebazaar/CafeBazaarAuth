package com.farsitel.bazaar.auth.callback;

import androidx.annotation.Nullable;

import com.farsitel.bazaar.auth.model.BazaarSignInAccount;

public interface BazaarSingInCallback {
    void onAccountReceived(@Nullable BazaarSignInAccount account);
}
