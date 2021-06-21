package com.farsitel.bazaar.util.ext

import android.util.Base64

internal fun ByteArray.toBase64(): String {
    return Base64.encodeToString(this, Base64.DEFAULT)
}

fun ByteArray.toReadableString(): String {
    return String(this)
}