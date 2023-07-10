package com.tetris.android.logic

import androidx.compose.ui.geometry.Offset

data class Sprite(
    val shape: List<Offset> = emptyList(),
    val offset: Offset = Offset(0f, 0f)
) {
    val location: List<Offset> = shape.map { it + offset }
}

val SpriteType = listOf(
    listOf(Offset(1f, -1f), Offset(1f, 0f), Offset(0f, 0f), Offset(0f, 1f)),    // Z
    listOf(Offset(0f, -1f), Offset(0f, 0f), Offset(1f, 0f), Offset(1f, 1f)),    // S
    listOf(Offset(0f, -1f), Offset(0f, 0f), Offset(0f, 1f), Offset(0f, 2f)),    // I
    listOf(Offset(0f, 1f), Offset(0f, 0f), Offset(0f, -1f), Offset(1f, 0f)),    // T
    listOf(Offset(1f, 0f), Offset(0f, 0f), Offset(1f, -1f), Offset(0f, -1f)),   // O
    listOf(Offset(0f, -1f), Offset(1f, -1f), Offset(1f, 0f), Offset(1f, 1f)),   // L
    listOf(Offset(1f, -1f), Offset(0f, -1f), Offset(0f, 0f), Offset(0f, 1f))    // J
)