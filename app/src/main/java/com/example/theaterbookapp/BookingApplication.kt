package com.example.theaterbookapp

import android.app.Application
import com.example.theaterbookapp.di.networkModule
import com.example.theaterbookapp.di.repositorySourceModule
import org.koin.core.context.startKoin

class BookingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {// Koin initialize
            modules(arrayListOf(networkModule, repositorySourceModule))
        }
    }
}