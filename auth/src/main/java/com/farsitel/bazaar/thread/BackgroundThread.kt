package com.farsitel.bazaar.thread

import android.os.Handler
import android.os.HandlerThread

internal class BackgroundThread : HandlerThread("ial") {

    init {
        start()
    }

    private val threadHandler = Handler(looper)

    fun execute(func: () -> Unit) {
        threadHandler.post {
            func.invoke()
        }
    }

}