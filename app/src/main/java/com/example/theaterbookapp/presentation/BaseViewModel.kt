package com.example.theaterbookapp.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theaterbookapp.data.core.Either
import com.example.theaterbookapp.data.core.MyException
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.*
import org.koin.core.KoinComponent

open class BaseViewModel : ViewModel(), KoinComponent {
    private val disposable = CompositeDisposable()

    val progressLiveData by lazy { MutableLiveData<Boolean>() } // loader visibility
    val errorLiveData by lazy { MutableLiveData<Exception>() } // Error dialogs

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)



    fun <R> postData(observer: MutableLiveData<R>, result: suspend () -> Either<MyException, R>) {
        progressLiveData.postValue(false)
        viewModelScope.launch(Dispatchers.IO) {
            progressLiveData.postValue(true)
            result.invoke().either({
                progressLiveData.postValue(false)
                errorLiveData.postValue(it)
            }, {
                progressLiveData.postValue(false)
                observer.postValue(it)
            })
        }


    }


    override fun onCleared() {
        viewModelJob.cancel()
        uiScope.coroutineContext.cancelChildren()
        disposable.dispose()
        super.onCleared()
    }
}