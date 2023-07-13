package com.tetris.android.logic.event

import com.tetris.android.logic.Direction

sealed interface GameEvent {
    data class Move(val direction: Direction): GameEvent    // 移动砖块
    object Reset: GameEvent     // 重置游戏
    object Pause: GameEvent     // 暂停游戏
    object Resume: GameEvent    // 恢复游戏
    object Rotate: GameEvent    // 旋转砖块
    object Drop: GameEvent      // 快速下落砖块
    object GameTick: GameEvent  // 更新游戏状态
    object Mute: GameEvent      // 游戏静音
}