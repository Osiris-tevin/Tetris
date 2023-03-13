package com.tetris.android.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import com.tetris.android.R
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tetris.android.ui.theme.BodyColor
import com.tetris.android.ui.theme.ScreenBackground

val DirectionButtonSize = 60.dp
val DropButtonSize = 90.dp

@Composable
fun GameBody(screen: @Composable () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(BodyColor)
    ) {
        Box(
            Modifier
                .size(400.dp, 450.dp)
                .padding(50.dp)
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                drawScreenBorder(
                    Offset(0f,0f),
                    Offset(size.width, 0f),
                    Offset(0f, size.height),
                    Offset(size.width, size.height)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp)
                    .background(ScreenBackground)
            ) {
                screen()
            }
        }

        Row(
            modifier = Modifier
                .padding(start = 40.dp, end = 40.dp)
                .height(160.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                // 按钮: 上
                GameButton(
                    modifier = Modifier.align(Alignment.TopCenter),
                    size = DirectionButtonSize
                ) {
                    ButtonText(modifier = it, text = stringResource(id = R.string.button_up))
                }
                // 按钮: 左
                GameButton(
                    modifier = Modifier.align(Alignment.CenterStart),
                    size = DirectionButtonSize
                ) {
                    ButtonText(modifier = it, text = stringResource(id = R.string.button_left))
                }
                // 按钮: 右
                GameButton(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    size = DirectionButtonSize
                ) {
                    ButtonText(modifier = it, text = stringResource(id = R.string.button_right))
                }
                // 按钮: 下
                GameButton(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    size = DirectionButtonSize
                ) {
                    ButtonText(modifier = it, text = stringResource(id = R.string.button_down))
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                GameButton(
                    modifier = Modifier
                        .align(Alignment.CenterEnd),
                    size = DropButtonSize
                ) {
                    ButtonText(modifier = it, text = stringResource(id = R.string.button_drop))
                }
            }
        }
    }
}

/**
 * 绘制屏幕边框
 */
fun DrawScope.drawScreenBorder(
    topLeft: Offset,
    topRight: Offset,
    bottomLeft: Offset,
    bottomRight: Offset
) {
    var path = Path().apply {
        moveTo(topLeft.x, topLeft.y)
        lineTo(topRight.x, topRight.y)
        lineTo(
            topRight.x / 2 + topLeft.x / 2,
            topLeft.y + topRight.x / 2 + topLeft.x / 2
        )
        lineTo(
            topRight.x / 2 + topLeft.x / 2,
            bottomLeft.y - topRight.x / 2 + topLeft.x / 2
        )
        lineTo(bottomLeft.x, bottomLeft.y)
        close()
    }
    drawPath(path, Color.Black.copy(0.5f))

    path = Path().apply {
        moveTo(bottomRight.x, bottomRight.y)
        lineTo(bottomLeft.x, bottomLeft.y)
        lineTo(
            topRight.x / 2 + topLeft.x / 2,
            bottomLeft.y - topRight.x / 2 + topLeft.x / 2
        )
        lineTo(
            topRight.x / 2 + topLeft.x / 2,
            topLeft.y + topRight.x / 2 + topLeft.x / 2
        )
        lineTo(topRight.x, topRight.y)
        close()
    }
    drawPath(path, Color.White.copy(0.5f))
}

@Preview
@Composable
fun PreViewGameBody() {
    GameBody {}
}