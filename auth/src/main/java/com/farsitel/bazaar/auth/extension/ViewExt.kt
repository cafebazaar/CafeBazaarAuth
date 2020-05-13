package com.farsitel.bazaar.auth.extension

import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View

fun View.setBackgroundApiAware(drawable: Drawable?) {
    if (isApiLevelAndUp(Build.VERSION_CODES.JELLY_BEAN)) {
        background = drawable
    } else {
        setBackgroundDrawable(drawable)
    }
}