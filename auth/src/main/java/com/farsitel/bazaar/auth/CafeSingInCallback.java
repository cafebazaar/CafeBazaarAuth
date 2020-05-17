package com.farsitel.bazaar.auth;

import androidx.annotation.Nullable;

import com.farsitel.bazaar.auth.model.CafeSignInAccount;

public interface CafeSingInCallback {
    void onAccountReceived(@Nullable CafeSignInAccount account);
}
