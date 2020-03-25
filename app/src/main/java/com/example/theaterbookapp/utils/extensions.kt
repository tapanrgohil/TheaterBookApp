package com.example.theaterbookapp.utils

import android.view.View
import android.view.View.*
import android.view.animation.CycleInterpolator
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.example.theaterbookapp.R
import com.google.gson.Gson
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*


fun View.gone() {
    visibility = GONE
}

fun View.inVisible() {
    visibility = INVISIBLE
}

fun View.visible() {
    visibility = VISIBLE
}

fun View.visible(boolean: Boolean) {
    if (boolean) {
        visible()
    } else {
        gone()
    }
}

fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(liveData: L, body: (T?) -> Unit) =
    liveData.observe(this, androidx.lifecycle.Observer(body))


fun AppCompatActivity.materialDialog(message: String, title: String = "") {
    MaterialDialog(this).cornerRadius(16f)
        .show {
            lifecycleOwner(this@materialDialog)
            if (title.isNotBlank()) {
                title(text = title)
            }
            message(text = message)
            positiveButton(R.string.ok) {
                it.dismiss()
            }
        }


}
