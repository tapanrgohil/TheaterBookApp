package com.example.theaterbookapp.data.core

/**
 * Converts exception to applocation readable exceptions
 */
sealed class MyException(val throwable: Throwable, val msg: String = "") : Exception() {

    public class UnKnownError(throwable: Throwable) : MyException(throwable)
    public class AutoApiErrorError(throwable: Throwable) : MyException(throwable)
    public class ApiError(throwable: Throwable) : MyException(throwable)
    public class NetworkErrorError(throwable: Throwable) : MyException(throwable)
    public class ValidationException(throwable: Throwable,val stringId:Int) : MyException(throwable)
}