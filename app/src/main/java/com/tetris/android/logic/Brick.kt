package com.tetris.android.logic

import androidx.compose.ui.geometry.Offset

/**
 * 砖块类
 */
data class Brick(val location: Offset = Offset(0f, 0f)) {
    fun offsetBy(step: Pair<Int, Int>) =
        copy(location = Offset(location.x + step.first, location.y + step.second))

    companion object {
        /**
         * 通过Offset列表创建Brick列表
         */
        private fun of(offsetList: List<Offset>) = offsetList.map { Brick(it) }

        fun of(sprite: BrickSprite) = of(sprite.location)

        fun of(xRange: IntRange, yRange: IntRange) =
            of(mutableListOf<Offset>().apply {
                xRange.forEach { x ->
                    yRange.forEach { y ->
                        this += Offset(x.toFloat(), y.toFloat())
                    }
                }
            })
    }
}