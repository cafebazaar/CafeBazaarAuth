package com.farsitel.bazaar.auth.connection

import android.content.Context

abstract class AuthConnection(context: Context) {
    abstract fun getLastAccountId(packageName: String)
}

fun buildAuthConnection(context: Context): AuthConnection {
    return ReceiverAuthConnection(context)
}