package com.tetris.android.ui

import android.graphics.Paint
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tetris.android.R
import com.tetris.android.logic.Brick
import com.tetris.android.logic.BrickSprite
import com.tetris.android.logic.NextMatrix
import com.tetris.android.logic.state.GameStatus
import com.tetris.android.ui.composable.LedClock
import com.tetris.android.ui.composable.LedNumber
import com.tetris.android.ui.theme.BrickMatrix
import com.tetris.android.ui.theme.BrickSprite
import com.tetris.android.ui.theme.ScreenBackground
import kotlin.math.min

@Composable
fun GameScreen(modifier: Modifier = Modifier) {
    val gameViewModel = viewModel<GameViewModel>()
    val state by gameViewModel.gameViewState.collectAsState()

    Box(
        modifier = modifier
            .background(Color.Black)
            .padding(1.dp)
            .background(ScreenBackground)
            .padding(10.dp)
    ) {
        val animateValue by rememberInfiniteTransition().animateFloat(
            initialValue = 0f,
            targetValue = 0.7f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1500),
                repeatMode = RepeatMode.Reverse
            )
        )
        
        Canvas(modifier = Modifier.fillMaxSize()) {
            val brickSize = min(
                size.width / state.matrix.first,
                size.height / state.matrix.second
            )

            drawMatrix(brickSize = brickSize, matrix = state.matrix)
            drawMatrixBorder(brickSize = brickSize, matrix = state.matrix)
            drawBricks(bricks = state.bricks, brickSize = brickSize, matrix = state.matrix)
            drawSprite(sprite = state.sprite, brickSize = brickSize, matrix = state.matrix)
            drawText(gameStatus = state.gameStatus, brickSize = brickSize, matrix = state.matrix, alpha = animateValue)
        }

        GameScoreboard(
            sprite = run {
                if (state.sprite == com.tetris.android.logic.BrickSprite.Empty) com.tetris.android.logic.BrickSprite.Empty
                else state.nextSprite.rotate()
            },
            score = state.score,
            line = state.line,
            level = state.level,
            isMute = state.isMute,
            isPaused = state.isPaused
        )
    }
}

/**
 * 游戏得分面板
 * @param modifier Modifier实例对象
 * @param brickSize 砖块大小
 * @param sprite BrickSprite实例对象
 * @param score 得分
 * @param line 消除行数
 * @param level 关卡等级
 * @param isMute 是否静音
 * @param isPaused 是否暂停
 */
@Composable
fun GameScoreboard(
    modifier: Modifier = Modifier,
    brickSize: Float = 35f,
    sprite: BrickSprite,
    score: Int = 0,
    line: Int = 0,
    level: Int = 1,
    isMute: Boolean = false,
    isPaused: Boolean = false
) {
    Row(modifier = modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.weight(0.65f))
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.35f)
        ) {
            // 得分
            Text(text = "Score", fontSize = 12.sp)
            LedNumber(modifier = Modifier.fillMaxWidth(), number = score, digits = 6)
            Spacer(modifier = Modifier.height(12.dp))
            // 消除行数
            Text(text = "Lines", fontSize = 12.sp)
            LedNumber(modifier = Modifier.fillMaxWidth(), number = line, digits = 6)
            Spacer(modifier = Modifier.height(12.dp))
            // 关卡等级
            Text(text = "Level", fontSize = 12.sp)
            LedNumber(modifier = Modifier.fillMaxWidth(), number = level, digits = 1)
            Spacer(modifier = Modifier.height(12.dp))
            // 下一种砖块
            Text(text = "Next", fontSize = 12.sp)
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp)
            ) {
                drawMatrix(brickSize = brickSize, matrix = NextMatrix)
                drawSprite(
                    sprite = sprite.adjustOffset(NextMatrix),
                    brickSize = brickSize,
                    matrix = NextMatrix
                )
            }
            // 游戏设置信息(静音/暂停/当前时间)
            Spacer(modifier = Modifier.weight(1f))
            Row {
                Image(
                    modifier = Modifier.width(15.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_music_off_24),
                    colorFilter = ColorFilter.tint(if (isMute) BrickSprite else BrickMatrix),
                    contentDescription = "Icon Music Off"
                )
                Image(
                    modifier = modifier.width(15.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_pause_24),
                    colorFilter = ColorFilter.tint(if (isPaused) BrickSprite else BrickMatrix),
                    contentDescription = "Icon Pause"
                )
                Spacer(modifier = Modifier.weight(1f))
                LedClock()
            }
        }
    }
}

/**
 * 根据游戏状态绘制提示文本
 * @param gameStatus 当前游戏状态
 * @param brickSize 砖块大小
 * @param matrix 矩阵大小
 * @param alpha 文本的透明度, 取值范围为0(完全透明)到1(完全不透明)
 */
private fun DrawScope.drawText(
    gameStatus: GameStatus,
    brickSize: Float,
    matrix: Pair<Int, Int>,
    alpha: Float
) {
    // 计算矩阵的中心位置
    val center = Offset(
        brickSize * matrix.first / 2,
        brickSize * matrix.second / 2
    )

    val drawText = { text: String, size: Float ->
        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.drawText(
                text,
                center.x,
                center.y,
                Paint().apply {
                    color = Color.Black.copy(alpha = alpha).toArgb()
                    textSize = size
                    textAlign = Paint.Align.CENTER
                    style = Paint.Style.FILL_AND_STROKE
                    strokeWidth =  size / 12
                }
            )
        }
    }

    // 根据游戏状态绘制相应的文本
    if (gameStatus == GameStatus.Greeting) {
        drawText("TETRIS", 80f)
    } else if (gameStatus == GameStatus.GameOver) {
        drawText("GAME OVER", 60f)
    }
}

/**
 * 绘制砖块矩阵
 * @param brickSize 砖块大小
 * @param matrix 矩阵大小
 */
private fun DrawScope.drawMatrix(
    brickSize: Float,
    matrix: Pair<Int, Int> // 横向、纵向的数量: 12 * 24
) {
    (0 until matrix.first).forEach { x ->
        (0 until matrix.second).forEach { y ->
            // 遍历调用drawBrick
            drawBrick(
                brickSize = brickSize,
                offset = Offset(x.toFloat(), y.toFloat()),
                color = BrickMatrix
            )
        }
    }
}

/**
 * 绘制砖块矩阵边框
 * @param brickSize 砖块大小
 * @param matrix 矩阵大小
 */
private fun DrawScope.drawMatrixBorder(
    brickSize: Float,
    matrix: Pair<Int, Int>
) {
    val gap = matrix.first * brickSize * 0.05f  // 边框与矩阵之间的间隙
    drawRect(
        color = Color.Black,
        size = Size(
            matrix.first * brickSize + gap,
            matrix.second * brickSize + gap
        ),
        topLeft = Offset(
            -gap / 2,
            -gap / 2
        ),
        style = Stroke(1.dp.toPx())
    )
}

/**
 * 绘制砖块单元
 * @param brickSize 砖块大小
 * @param offset 砖块在矩阵中的偏移位置
 * @param color 砖块颜色
 */
private fun DrawScope.drawBrick(
    brickSize: Float,
    offset: Offset,
    color: Color
) {
    // 根据Offset计算实际位置
    val actualLocation = Offset(
        offset.x * brickSize,
        offset.y * brickSize
    )
    val outerSize = brickSize * 0.8f
    val outerOffset = (brickSize - outerSize) / 2
    // 绘制外部矩形边框
    drawRect(
        color = color,
        topLeft = actualLocation + Offset(outerOffset, outerOffset),
        size = Size(outerSize, outerSize),
        style = Stroke(outerSize / 10)
    )
    val innerSize = brickSize * 0.5f
    val innerOffset = (brickSize - innerSize) / 2
    // 绘制内部矩形砖块
    drawRect(
        color = color,
        topLeft = actualLocation + Offset(innerOffset, innerOffset),
        size = Size(innerSize, innerSize)
    )
}

/**
 * 绘制已有的砖块
 * @param bricks 已有砖块的列表
 * @param brickSize 砖块大小
 * @param matrix 矩阵大小
 */
private fun DrawScope.drawBricks(
    bricks: List<Brick>,
    brickSize: Float,
    matrix: Pair<Int, Int>
) {
    clipRect(
        left = 0f,
        top = 0f,
        right = matrix.first * brickSize,
        bottom = matrix.second * brickSize
    ) {
        bricks.forEach { brick ->
            drawBrick(brickSize = brickSize, offset = brick.location, color = BrickSprite)
        }
    }
}

/**
 * 绘制下落砖块
 * @param sprite 下落砖块对象, 包含位置信息
 * @param brickSize 砖块大小
 * @param matrix 矩阵大小
 */
private fun DrawScope.drawSprite(
    sprite: BrickSprite,
    brickSize: Float,
    matrix: Pair<Int, Int>
) {
    clipRect(
        0f, 0f,
        matrix.first * brickSize,
        matrix.second * brickSize
    ) {
        sprite.location.forEach {
            drawBrick(
                brickSize = brickSize,
                offset = Offset(it.x, it.y),
                BrickSprite
            )
        }
    }
}