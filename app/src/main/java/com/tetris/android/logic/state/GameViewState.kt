package com.tetris.android.logic.state

import com.tetris.android.logic.Brick
import com.tetris.android.logic.sprite.BrickSprite
import com.tetris.android.logic.util.Constants.MatrixHeight
import com.tetris.android.logic.util.Constants.MatrixWidth
import kotlin.math.min

data class GameViewState(
    val bricks: List<Brick> = emptyList(),
    val sprite: BrickSprite = BrickSprite.Empty,
    val randomSprites: List<BrickSprite> = emptyList(),
    val matrix: Pair<Int, Int> = MatrixWidth to MatrixHeight,
    val gameStatus: GameStatus = GameStatus.Greeting,
    val score: Int = 0,
    val line: Int = 0,
    val isMute: Boolean = false
) {
    val level: Int
        get() = min(10, 1 + line / 20)

    val nextSprite: BrickSprite
        get() = randomSprites.firstOrNull() ?: BrickSprite.Empty

    val isPausing
        get() = gameStatus == GameStatus.Pausing

    val isRunning
        get() = gameStatus == GameStatus.Running
}

enum class GameStatus {
    Greeting,       // 欢迎动画中
    Running,        // 游戏进行中
    LineClearing,   // 消行动画中
    Pausing,        // 游戏暂停中
    ScreenClearing, // 清屏动画中
    GameOver        // 游戏结束
}