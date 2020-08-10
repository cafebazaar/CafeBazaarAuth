package com.farsitel.bazaar.storage.callback;

import androidx.annotation.Nullable;

import com.farsitel.bazaar.BazaarResponse;

public interface BazaarStorageCallback {
    void onDataReceived(@Nullable BazaarResponse<byte[]> response);
}
