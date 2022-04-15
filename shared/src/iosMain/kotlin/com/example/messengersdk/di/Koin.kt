package com.example.messengersdk.di

import com.example.messengersdk.database.DatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module(createdAtStart = true) {
    single {
        DatabaseDriverFactory()
    }
}