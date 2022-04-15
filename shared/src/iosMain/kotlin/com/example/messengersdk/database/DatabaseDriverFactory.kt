package com.example.messengersdk.database

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import tables.AppDatabase

actual class DatabaseDriverFactory() {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(AppDatabase.Schema, "recipes1.db")
    }
}