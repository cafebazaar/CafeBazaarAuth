package com.farsitel.bazaar.core.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.farsitel.bazaar.core.R

class LoginButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {

        val attr = context.obtainStyledAttributes(
            attrs,
            R.styleable.LoginButton,
            defStyleAttr,
            0
        )

        val viewSize = LoadingButtonSize.fromXml(
            attr.getInt(
                R.styleable.LoginButton_login_button_size,
                LoadingButtonSize.NORMAL.ordinal
            )
        )

        attr.recycle()
        addViewBasedOnViewSize(viewSize)
    }

    private fun addViewBasedOnViewSize(viewSize: LoadingButtonSize) {
        removeAllViews()
        when (viewSize) {
            LoadingButtonSize.NORMAL -> addViewByLayoutId(R.layout.login_button_normal)
            LoadingButtonSize.BIG -> addViewByLayoutId(R.layout.login_button_big)
        }
    }

    private fun addViewByLayoutId(layoutId: Int) {
        LayoutInflater.from(context).inflate(layoutId, this, true)
    }

    enum class LoadingButtonSize {
        NORMAL,
        BIG;

        companion object {
            fun fromXml(xmlSize: Int) = when (xmlSize) {
                0 -> NORMAL
                1 -> BIG
                else -> NORMAL
            }
        }
    }
}