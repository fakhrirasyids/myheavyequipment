package com.project.myheavyequipment.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.project.myheavyequipment.R

class CustomEtEmail : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        addTextChangedListener(onTextChanged = { email, _, _, _ ->
            error = if (!email.isNullOrEmpty()) {
                if (!Patterns.EMAIL_ADDRESS.matcher(email.toString()).matches()) {
                    "Format email tidak benar!"
                } else null
            } else null
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = "Alamat Email"
        background = ContextCompat.getDrawable(context, R.drawable.background_form)
    }
}