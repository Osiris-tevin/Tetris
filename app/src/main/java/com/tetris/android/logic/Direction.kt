package com.tetris.android.logic

enum class Direction {
    Left, Up, Right, Down
}

fun Direction.toOffset() = when(this) {
    Direction.Left -> -1 to 0
    Direction.Up -> 0 to -1
    Direction.Right -> 1 to 0
    Direction.Down -> 0 to 1
}
