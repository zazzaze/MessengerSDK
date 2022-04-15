package com.example.messengersdk.network.http

import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.QualifierValue

internal object HostQualifier : Qualifier {
    override val value: QualifierValue
        get() = "http://localhost:8070"
}