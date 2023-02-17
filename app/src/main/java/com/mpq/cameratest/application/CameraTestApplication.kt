package com.mpq.cameratest.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CameraTestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}