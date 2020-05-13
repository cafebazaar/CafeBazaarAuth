package com.farsitel.bazaar.auth.extension

import android.os.Build

fun isApiLevelAndUp(versionCode: Int): Boolean = Build.VERSION.SDK_INT >= versionCode