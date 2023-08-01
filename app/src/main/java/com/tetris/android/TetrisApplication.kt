package com.tetris.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.tetris.android.logic.util.SoundUtil

class TetrisApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        SoundUtil.init()
    }

}