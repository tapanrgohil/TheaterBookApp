package com.example.theaterbookapp.data.core

import com.google.gson.annotations.SerializedName


data class BaseRS<T>(

    @SerializedName("status")
    val status: String,
    @SerializedName("StatusCode")
    val StatusCode: String,
    @SerializedName("Data")
    val data: T
)

