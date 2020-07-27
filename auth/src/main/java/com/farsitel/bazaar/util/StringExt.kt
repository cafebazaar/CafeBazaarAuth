package com.farsitel.bazaar.util

import android.util.Base64

internal fun String.fromBase64(): ByteArray {
    return Base64.decode(this, Base64.DEFAULT)
}