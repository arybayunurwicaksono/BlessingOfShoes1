package com.example.blessingofshoes_1.utils

import android.app.Application
import com.mazenrashed.printooth.Printooth
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        Printooth.init(this)
    }
}