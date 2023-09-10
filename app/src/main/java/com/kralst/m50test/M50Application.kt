package com.kralst.m50test

import android.app.Application
import timber.log.Timber

// TODO:
//  - proguard rules
class M50Application: Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}