package com.farsitel.bazaar.auth.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.farsitel.bazaar.auth.R
import com.farsitel.bazaar.auth.extension.setBackgroundApiAware

class LoginButton @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatButton(context, attributeSet, defStyleAttr) {

    init {
        setBackgroundApiAware(
            ContextCompat.getDrawable(
                context,
                R.drawable.shape_button_flat
            )
        )
        text = context.getString(R.string.login_to_bazaar)
    }
}