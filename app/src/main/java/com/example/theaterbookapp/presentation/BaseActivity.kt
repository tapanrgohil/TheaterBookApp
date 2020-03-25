package com.example.theaterbookapp.presentation

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.theaterbookapp.R
import com.example.theaterbookapp.data.core.MyException
import com.example.theaterbookapp.utils.materialDialog
import com.github.loadingview.LoadingDialog
import java.lang.reflect.ParameterizedType

abstract class BaseActivity<T : BaseViewModel> : AppCompatActivity() {

    private lateinit var baseViewModel: T

    open fun getViewModel(): T {
        if (!::baseViewModel.isInitialized) {
            baseViewModel = ViewModelProviders.of(this)
                .get((this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>)
        }
        return baseViewModel
    }


    abstract fun attachLiveData()

    var loadingDialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        }
    }


    override fun onResume() {
        super.onResume()


        baseViewModel = getViewModel()
        baseViewModel.progressLiveData.value = false

        baseViewModel.progressLiveData.observe(this, Observer {
            if (it) {
                if (loadingDialog == null)
                    loadingDialog = LoadingDialog.get(this).show()
                else
                    loadingDialog?.show()
            } else {
                loadingDialog?.hide()
            }

        })

        baseViewModel.errorLiveData.observe(this, Observer {
            baseViewModel.progressLiveData.value = false
            loadingDialog?.hide()
            it?.apply {
                if (it is MyException) {
                    when (it) {
                        is MyException.UnKnownError -> {
                            materialDialog(it.throwable.localizedMessage.orEmpty())
                        }

                        is MyException.NetworkErrorError -> {
                            Toast.makeText(
                                this@BaseActivity,
                                getString(R.string.connection_error),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                        is MyException.ApiError -> {
                            Log.d("ApiError", it.throwable.localizedMessage)
                        }
                        is MyException.AutoApiErrorError -> {
                            Log.d("AutoApiErrorError", it.throwable.localizedMessage)
                            materialDialog(it.throwable.localizedMessage.orEmpty())
                        }
                        is MyException.ValidationException -> {
                            materialDialog(getString(it.stringId))
                        }
                        else -> {
                            Log.d("IAgreeException", it.throwable.localizedMessage)
                            materialDialog(it.throwable.localizedMessage.orEmpty())
                        }
                    }


                } else {
                    Log.e("Error", it.localizedMessage, it)
                }
                baseViewModel.errorLiveData.value = null
            }
        })
        attachLiveData()
    }


    override fun onPause() {
        baseViewModel.errorLiveData.value = null
        baseViewModel.progressLiveData.value = false
        super.onPause()
    }

}