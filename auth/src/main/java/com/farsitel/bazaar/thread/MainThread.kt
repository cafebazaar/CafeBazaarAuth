package com.farsitel.bazaar.thread

import android.os.Handler
import android.os.Looper

class MainThread : Handler(Looper.getMainLooper())