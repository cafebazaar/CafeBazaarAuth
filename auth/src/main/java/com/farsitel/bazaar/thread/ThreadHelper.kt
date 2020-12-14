package com.farsitel.bazaar.thread

import android.os.Looper

internal object ThreadHelper {

    fun changeToMainThreadIfNeeded(mainThread: MainThread, task: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            task.invoke()
        } else {
            mainThread.post(Runnable(task))
        }
    }
}