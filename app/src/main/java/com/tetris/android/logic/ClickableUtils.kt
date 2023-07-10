package com.tetris.android.logic

// Clickable数据类, 用于存储各种点击事件的回调函数
data class Clickable constructor(
    val onMove: (Direction) -> Unit,// 移动
    val onRotate: () -> Unit,       // 旋转
    val onRestart: () -> Unit,      // 重新开始
    val onPause: () -> Unit,        // 暂停
    val onMute: () -> Unit          // 静音
)

// combinedClickable函数, 用于创建Clickable对象的实例
fun combinedClickable(
    onMove: (Direction) -> Unit = {},
    onRotate: () -> Unit = {},
    onRestart: () -> Unit = {},
    onPause: () -> Unit = {},
    onMute: () -> Unit = {}
) = Clickable(onMove, onRotate, onRestart, onPause, onMute)