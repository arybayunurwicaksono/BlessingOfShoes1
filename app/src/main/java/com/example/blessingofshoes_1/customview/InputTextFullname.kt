package com.example.blessingofshoes_1.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.blessingofshoes_1.R

class InputTextFullname: AppCompatEditText {

    private lateinit var drawable: Drawable

    constructor(context: Context) : super(context) { init() }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init() }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) { init() }

    private fun setDrawable(
        start: Drawable? = null,
        top: Drawable? = null,
        end: Drawable? = null,
        bottom: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom)
    }
    private fun init() {
        drawable =
            ContextCompat.getDrawable(context, R.drawable.ic_baseline_assignment_ind_24) as Drawable
        inputType = InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE
        compoundDrawablePadding = 16

        setHint(R.string.full_name)
        setAutofillHints(AUTOFILL_HINT_NAME)
        setDrawable(drawable)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                setError(null)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrBlank())
                    error = context.getString(R.string.fullname_required)
            }
        })
    }
}