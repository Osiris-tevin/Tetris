package com.tetris.android.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.tooling.preview.Preview
import com.tetris.android.logic.BricksSprite
import com.tetris.android.ui.theme.BrickMatrix
import com.tetris.android.ui.theme.BrickSprite

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

/**
 * 绘制砖块矩阵
 */
private fun DrawScope.drawMatrix(
    brickSize: Float,
    matrix: Pair<Int, Int> // 横向、纵向的数量: 12 * 24
) {
    (0 until matrix.first).forEach { x ->
        (0 until matrix.second).forEach { y ->
            // 遍历调用drawBrick
            drawBrick(brickSize, Offset(x.toFloat(), y.toFloat()), BrickMatrix)
        }
    }
}

/**
 * 绘制砖块单元
 */
private fun DrawScope.drawBrick(
    brickSize: Float,   // 每一个方块的size
    offset: Offset,     // 在矩阵中偏移的位置
    color: Color        // 砖块颜色
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
    // 绘制内部矩形方块
    drawRect(
        color = color,
        topLeft = actualLocation + Offset(innerOffset, innerOffset),
        size = Size(innerSize, innerSize)
    )
}

/**
 * 绘制下落砖块
 */
fun DrawScope.drawBricksSprite(
    brickSprite: BricksSprite,
    brickSize: Float,
    matrix: Pair<Int, Int>
) {
    clipRect(
        0f,
        0f,
        matrix.first * brickSize,
        matrix.second * brickSize
    ) {
        brickSprite.location.forEach {
            drawBrick(
                brickSize,
                Offset(it.x, it.y),
                BrickSprite
            )
        }
    }
}