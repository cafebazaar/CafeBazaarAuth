package com.farsitel.bazaar.storage.callback;

import androidx.annotation.Nullable;

public interface BazaarStorageCallback {
    void onDataReceived(@Nullable String data);
}
