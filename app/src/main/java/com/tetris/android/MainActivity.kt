package com.tetris.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.tetris.android.ui.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // DecorView不再为SystemUI(状态栏和导航栏)预留padding
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        // 状态栏设置为亮色(状态栏的文字、图标颜色为亮色)
        controller.isAppearanceLightStatusBars = false

        setContent {
            HomeScreen()
        }
    }
}