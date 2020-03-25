package com.example.theaterbookapp.di

import com.example.theaterbookapp.data.BookingRepo
import com.example.theaterbookapp.data.BookingRepoImpl
import org.koin.dsl.module


/**
 * Injecting repositories
 */
val repositorySourceModule = module {

    single<BookingRepo> { BookingRepoImpl(get()) }
}