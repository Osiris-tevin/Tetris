package com.tetris.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tetris.android.ui.theme.Purple200
import com.tetris.android.ui.theme.Purple500

@Composable
fun GameButton(
    modifier: Modifier = Modifier,
    size: Dp,
    content: @Composable (Modifier) -> Unit
) {
    val backgroundShape = RoundedCornerShape(size / 2)
    Box(
        modifier = modifier
            .shadow(5.dp, shape = backgroundShape)
            .size(size = size)
            .clip(backgroundShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Purple200,
                        Purple500
                    ),
                    startY = 0f,
                    endY = 80f
                )
            )
    ) {
        content(Modifier.align(Alignment.Center))
    }
}

@Composable
fun ButtonText(modifier: Modifier, text: String) {
    Text(
        modifier = modifier,
        text = text,
        color = Color.White.copy(0.9f),
        fontSize = 18.sp
    )
}