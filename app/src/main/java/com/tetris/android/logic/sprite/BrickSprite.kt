package com.tetris.android.logic.sprite

import androidx.compose.ui.geometry.Offset
import com.tetris.android.logic.Brick
import kotlin.math.absoluteValue
import kotlin.random.Random

/**
 * 砖块Sprite类
 */
data class BrickSprite(
    val shape: List<Offset> = emptyList(),
    val offset: Offset = Offset(0f, 0f)
) {
    val location: List<Offset> = shape.map { it + offset }

    /**
     * 根据给定步长移动砖块
     * @param step 移动的步长, 表示为Pair(x, y)
     * @return 移动后新的BrickSprite对象
     */
    fun moveBy(step: Pair<Int, Int>): BrickSprite =
        copy(offset = offset + Offset(step.first.toFloat(), step.second.toFloat()))

    /**
     * 旋转砖块
     * @return 旋转后新的BrickSprite对象
     */
    fun rotate(): BrickSprite {
        val newShape = shape.toMutableList()
        for (i in shape.indices) {
            newShape[i] = Offset(shape[i].y, -shape[i].x)
        }
        return copy(shape = newShape)
    }

    /**
     * 调整砖块位置, 使其适应给定的矩阵范围
     * @param matrix 矩阵的尺寸, 表示为Pair(width, height)
     * @param adjustY 是否调整Y轴偏移量, 默认为true
     * @return 调整偏移量后新的BrickSprite对象
     */
    fun adjustOffset(matrix: Pair<Int, Int>, adjustY: Boolean = true): BrickSprite {
        val yOffset =
            if (adjustY)
                    (location.minByOrNull { it.y }?.y?.takeIf { it < 0 }?.absoluteValue ?: 0).toInt() +
                            (location.maxByOrNull { it.y }?.y?.takeIf { it > matrix.second - 1 }
                                ?.let { matrix.second - it - 1 } ?: 0).toInt()
            else 0

        val xOffset =
            (location.minByOrNull { it.x }?.x?.takeIf { it < 0 }?.absoluteValue ?: 0).toInt() +
                (location.maxByOrNull { it.x }?.x?.takeIf { it > matrix.first - 1 }
                    ?.let { matrix.first - it - 1 } ?: 0).toInt()
        return moveBy(xOffset to yOffset)
    }

    companion object {
        val Empty = BrickSprite()
    }
}

/**
 * 砖块类型
 */
val BrickSpriteType = listOf(
    listOf(Offset(1f, -1f), Offset(1f, 0f), Offset(0f, 0f), Offset(0f, 1f)),    // Z
    listOf(Offset(0f, -1f), Offset(0f, 0f), Offset(1f, 0f), Offset(1f, 1f)),    // S
    listOf(Offset(0f, -1f), Offset(0f, 0f), Offset(0f, 1f), Offset(0f, 2f)),    // I
    listOf(Offset(0f, 1f), Offset(0f, 0f), Offset(0f, -1f), Offset(1f, 0f)),    // T
    listOf(Offset(1f, 0f), Offset(0f, 0f), Offset(1f, -1f), Offset(0f, -1f)),   // O
    listOf(Offset(0f, -1f), Offset(1f, -1f), Offset(1f, 0f), Offset(1f, 1f)),   // L
    listOf(Offset(1f, -1f), Offset(0f, -1f), Offset(0f, 0f), Offset(0f, 1f))    // J
)

/**
 * 判断砖块在矩阵中是否合法
 * @param bricks 已有砖块的列表
 * @param matrix 矩阵的尺寸, 表示为Pair(width, height)
 * @return 如果砖块在矩阵中处于合法位置, 返回true; 否则返回false.
 */
fun BrickSprite.isValidInMatrix(bricks: List<Brick>, matrix: Pair<Int, Int>): Boolean {
    return location.none { location ->
        location.x < 0 || location.x > matrix.first - 1 || location.y > matrix.second - 1 ||
                bricks.any { it.location.x == location.x && it.location.y == location.y }
    }
}

/**
 * 生成一个顺序随机的BrickSprite列表
 * @param matrix 矩阵的尺寸, 表示为Pair(width, height)
 * @return 顺序随机的BrickSprite列表
 */
fun generateRandomBrickSprites(matrix: Pair<Int, Int>): List<BrickSprite> {
    return BrickSpriteType.map { shape ->
        BrickSprite(shape, Offset(Random.nextInt(matrix.first - 1).toFloat(), (-1).toFloat())).adjustOffset(matrix, false)
    }.shuffled()
}