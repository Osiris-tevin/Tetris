package com.tetris.android.ui.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tetris.android.logic.Brick
import com.tetris.android.logic.Direction
import com.tetris.android.logic.event.GameEvent
import com.tetris.android.logic.sprite.BrickSprite
import com.tetris.android.logic.sprite.generateRandomBrickSprites
import com.tetris.android.logic.sprite.isValidInMatrix
import com.tetris.android.logic.state.GameStatus
import com.tetris.android.logic.state.GameViewState
import com.tetris.android.logic.toOffset
import com.tetris.android.logic.util.Constants.ScoreEverySprite
import com.tetris.android.logic.util.SoundType
import com.tetris.android.logic.util.SoundUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameViewModel : ViewModel() {

    private val _gameViewState: MutableState<GameViewState> = mutableStateOf(GameViewState())

    val gameViewState: State<GameViewState> = _gameViewState

    fun dispatch(event: GameEvent) = reduce(gameViewState.value, event)

    private fun reduce(state: GameViewState, event: GameEvent) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                emit(when (event) {
                    // 移动砖块
                    is GameEvent.Move -> run {
                        if (!state.isRunning) return@run state

                        SoundUtil.play(state.isMute, SoundType.Move)
                        val offset = event.direction.toOffset()
                        val sprite = state.sprite.moveBy(offset)

                        if (sprite.isValidInMatrix(state.bricks, state.matrix)) {
                            state.copy(sprite = sprite)
                        } else {
                            state
                        }
                    }
                    // 重置游戏
                    GameEvent.Reset -> run {
                        if (state.gameStatus == GameStatus.Greeting || state.gameStatus == GameStatus.GameOver)
                            return@run GameViewState(
                                gameStatus = GameStatus.Running,
                                isMute = state.isMute
                            )
                        state.copy(gameStatus = GameStatus.ScreenClearing).also {
                            launch {
                                clearScreen(state = state)
                                emit(
                                    GameViewState(
                                        gameStatus = GameStatus.Greeting,
                                        isMute = state.isMute
                                    )
                                )
                            }
                        }
                    }
                    // 暂停游戏
                    GameEvent.Pause -> if (state.isRunning) {
                        state.copy(gameStatus = GameStatus.Pausing)
                    } else state
                    // 恢复游戏
                    GameEvent.Resume -> if (state.isPausing) {
                        state.copy(gameStatus = GameStatus.Running)
                    } else state
                    // 旋转砖块
                    GameEvent.Rotate -> run {
                        if (!state.isRunning) return@run state

                        SoundUtil.play(state.isMute, SoundType.Rotate)
                        val sprite = state.sprite.rotate().adjustOffset(state.matrix)

                        if (sprite.isValidInMatrix(bricks = state.bricks, matrix = state.matrix)) {
                            state.copy(sprite = sprite)
                        } else {
                            state
                        }
                    }
                    // 快速下落砖块
                    GameEvent.Drop -> run {
                        if (!state.isRunning) return@run state

                        SoundUtil.play(state.isMute, SoundType.Drop)
                        val sprite = generateSequence(0) { it + 1 }
                            .map { state.sprite.moveBy(0 to it) }
                            .firstOrNull { !it.isValidInMatrix(bricks = state.bricks, matrix = state.matrix) }
                            ?.moveBy(0 to -1)
                            ?: state.sprite

                        state.copy(sprite = sprite)
                    }
                    // 更新游戏状态
                    GameEvent.GameTick -> run {
                        if (!state.isRunning) return@run state

                        // 砖块Sprite继续下落
                        if (state.sprite != BrickSprite.Empty) {
                            val sprite = state.sprite.moveBy(Direction.Down.toOffset())
                            if (sprite.isValidInMatrix(bricks = state.bricks, matrix = state.matrix)) {
                                return@run state.copy(sprite = sprite)
                            }
                        }

                        // 游戏结束
                        if (!state.sprite.isValidInMatrix(bricks = state.bricks, matrix = state.matrix)) {
                            return@run state.copy(
                                gameStatus = GameStatus.ScreenClearing
                            ).also {
                                launch {
                                    emit(
                                        clearScreen(state = state).copy(gameStatus = GameStatus.GameOver)
                                    )
                                }
                            }
                        }

                        // NextSprite
                        val (updatedBricks, clearedLines) = updateBricks(
                            currentBricks = state.bricks,
                            sprite = state.sprite,
                            matrix = state.matrix
                        )
                        val (unClearedBricks, clearingBricks, clearedBricks) = updatedBricks
                        val newState = state.copy(
                            sprite = state.nextSprite,
                            randomSprites = (state.randomSprites - state.nextSprite).takeIf { it.isNotEmpty() }
                                ?: generateRandomBrickSprites(state.matrix),
                            score = state.score + calculateScoreFromClearedLines(clearedLines) +
                                    if (state.sprite != BrickSprite.Empty) ScoreEverySprite else 0,
                            line = state.line + clearedLines
                        )

                        if (clearedLines != 0) {
                            SoundUtil.play(state.isMute, SoundType.Clean)
                            state.copy(
                                gameStatus = GameStatus.LineClearing
                            ).also {
                                launch {
                                    repeat(5) {
                                        emit(
                                            state.copy(
                                                bricks = if (it % 2 == 0) unClearedBricks else clearingBricks,  // 闪烁效果
                                                gameStatus = GameStatus.LineClearing,
                                                sprite = BrickSprite.Empty
                                            )
                                        )
                                        delay(100)
                                    }
                                    emit(
                                        newState.copy(
                                            bricks = clearedBricks,
                                            gameStatus = GameStatus.Running
                                        )
                                    )
                                }
                            }
                        } else {
                            newState.copy(bricks = unClearedBricks)
                        }
                    }
                    // 游戏静音
                    GameEvent.Mute -> state.copy(isMute = !state.isMute)
                })
            }
        }
    }

    private fun emit(state: GameViewState) {
        _gameViewState.value = state
    }

    /**
     * 清空屏幕上所有的砖块
     */
    private suspend fun clearScreen(state: GameViewState): GameViewState {
        val xRange = 0 until state.matrix.first
        var newState = state

        SoundUtil.play(state.isMute, SoundType.Start)
        (state.matrix.second downTo 0).forEach { y ->
            emit(
                state.copy(
                    gameStatus = GameStatus.ScreenClearing,
                    bricks = state.bricks + Brick.of(
                        xRange, y until state.matrix.second
                    )
                )
            )
            delay(50)
        }
        (0..state.matrix.second).forEach { y ->
            emit(
                state.copy(
                    gameStatus = GameStatus.ScreenClearing,
                    bricks = Brick.of(xRange, y until state.matrix.second),
                    sprite = BrickSprite.Empty
                ).also { newState = it }
            )
            delay(50)
        }

        return newState
    }

    /**
     * 更新砖块状态
     * @param currentBricks 当前的砖块列表
     * @param sprite BrickSprite实例对象
     * @param matrix 矩阵大小, 表示为Pair(width, height)
     * @return 返回一个[Pair], 第一个元素是一个[Triple], 第二个元素是消除的行数
     * - [Triple.first]: 合并后的砖块列表(尚未进行任何消除操作)
     * - [Triple.second]: 消除满行后的砖块列表
     * - [Triple.third]: 消除满行并偏移其他砖块后的砖块列表
     */
    private fun updateBricks(
        currentBricks: List<Brick>,
        sprite: BrickSprite,
        matrix: Pair<Int, Int>
    ): Pair<Triple<List<Brick>, List<Brick>, List<Brick>>, Int> {
        // 获取合并后的砖块信息
        val bricks: List<Brick> = (currentBricks + Brick.of(sprite))

        // 使用可变的Map存储砖块的位置信息, 以行为键, 列的集合为值
        val map = mutableMapOf<Float, MutableSet<Float>>()
        bricks.forEach { brick ->
            map.getOrPut(brick.location.y) {
                mutableSetOf()
            }.add(brick.location.x)
        }

        var clearingBricks: List<Brick> = bricks    // 已经消除满行但尚未进行其它砖块偏移的砖块列表
        var clearedBricks: List<Brick> = bricks     // 已经消除满行且已经完成其它砖块偏移的砖块列表
        // 根据砖块位置的Map进行排序, 以准备清除满行
        val clearLines = map.entries.sortedBy { it.key }
            .filter { it.value.size == matrix.first }.map { it.key }
            .onEach { line ->
                // 清除行
                clearingBricks = clearingBricks.filter { brick ->
                    brick.location.y != line
                }
                // 清除行并偏移砖块
                clearedBricks = clearedBricks.filter { brick ->
                    brick.location.y != line
                }.map { brick ->
                    if (brick.location.y < line) brick.offsetBy(0 to 1) else brick
                }
            }

        // 返回[Triple]和消除的行数
        return Triple(bricks, clearingBricks, clearedBricks) to clearLines.size
    }

    /**
     * 根据消除的行数计算分数
     * @param lines 消除的行数
     * @return 对应的分数值
     */
    private fun calculateScoreFromClearedLines(lines: Int) = when (lines) {
        1 -> 100
        2 -> 300
        3 -> 700
        4 -> 1500
        else -> 0
    }

}