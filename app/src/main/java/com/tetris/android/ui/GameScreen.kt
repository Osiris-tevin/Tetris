package com.tetris.android.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tetris.android.logic.BrickSprite
import com.tetris.android.logic.NextMatrix
import com.tetris.android.ui.composable.LedNumber
import com.tetris.android.ui.theme.BrickMatrix
import com.tetris.android.ui.theme.BrickSprite
import com.tetris.android.R
import com.tetris.android.ui.composable.LedClock

@Composable
fun GameScreen(modifier: Modifier = Modifier) {
    GameScoreboard(sprite = com.tetris.android.logic.BrickSprite.Empty)
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

@Preview(showBackground = true)
@Composable
fun BrickMatrixPreview() {
    val brickSize = 50f
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val pair = Pair(12, 24)
        drawMatrix(brickSize, pair)
    }
}