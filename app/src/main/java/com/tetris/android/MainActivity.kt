package com.tetris.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.tetris.android.ui.PreViewGameBody

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // DecorView不再为SystemUI(状态栏和导航栏)预留Padding
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        // 状态栏设置为亮色(状态栏的文字、图表颜色为暗色)
        controller?.isAppearanceLightStatusBars = true

        setContent {
            PreViewGameBody()
        }
    }
}