package com.tetris.android.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tetris.android.R
import com.tetris.android.ui.theme.*

val DirectionButtonSize = 60.dp
val DropButtonSize = 90.dp

@Preview
@Composable
fun HomeScreen() {
    GameBody(screen = {})
}

@Composable
fun GameBody(screen: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GameBodyColor)
    ) {
        Box(
            modifier = Modifier
                .size(400.dp, 450.dp)
                .padding(50.dp)
        ) {
            // 绘制屏幕边框
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawScreenBorder(
                    Offset(0f, 0f),
                    Offset(size.width, 0f),
                    Offset(0f, size.height),
                    Offset(size.width, size.height)
                )
            }
            // 绘制屏幕
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp)
                    .background(ScreenBackground)
            ) {
                screen()
            }
        }
        // 游戏按钮
        GameButtons()
        // 版本信息
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.BottomCenter
        ) {
            GameVersionInfo()
        }
    }
}

@Composable
fun GameButtons() {
    Row(
        modifier = Modifier
            .height(160.dp)
            .padding(horizontal = 40.dp)
    ) {
        // 方向按钮
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
                GameButtonText(modifier = it, text = stringResource(id = R.string.button_up))
            }
            // 按钮: 左
            GameButton(
                modifier = Modifier.align(Alignment.CenterStart),
                size = DirectionButtonSize
            ) {
                GameButtonText(modifier = it, text = stringResource(id = R.string.button_left))
            }
            // 按钮: 右
            GameButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                size = DirectionButtonSize
            ) {
                GameButtonText(modifier = it, text = stringResource(id = R.string.button_right))
            }
            // 按钮: 下
            GameButton(
                modifier = Modifier.align(Alignment.BottomCenter),
                size = DirectionButtonSize
            ) {
                GameButtonText(modifier = it, text = stringResource(id = R.string.button_down))
            }
        }
        // 下落按钮
        Box (
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            GameButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                size = DropButtonSize
            ) {
                GameButtonText(modifier = it, text = stringResource(id = R.string.button_drop))
            }
        }
    }
}

@Composable
fun GameButton(
    modifier: Modifier = Modifier,
    size: Dp,
    content: @Composable (Modifier) -> Unit
) {
    val backgroundShape = RoundedCornerShape(size / 2)
    Box(
        modifier = modifier
            .size(size = size)
            .clip(backgroundShape)
            .shadow(elevation = 5.dp, shape = backgroundShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GameButtonColorStart,
                        GameButtonColorEnd
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
fun GameButtonText(modifier: Modifier, text: String) {
    Text(
        text = text,
        style = button,
        color = Color.White.copy(0.9f),
        fontSize = 18.sp,
        modifier = modifier
    )
}

@Composable
fun GameVersionInfo() {
    Text(
        text = "版本信息: " + stringResource(id = R.string.app_version),
        style = h3,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(10.dp)
    )
}

/**
 * 绘制屏幕边框
 * @param topLeft 左上角的偏移量
 * @param topRight 右上角的偏移量
 * @param bottomLeft 左下角的偏移量
 * @param bottomRight 右下角的偏移量
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
        lineTo(bottomLeft.x, bottomLeft.y)
        close()
    }
    drawPath(path = path, color = Color.Black.copy(alpha = 0.5f))

    path = Path().apply {
        moveTo(bottomRight.x, bottomRight.y)
        lineTo(bottomLeft.x, bottomLeft.y)
        lineTo(topRight.x, topRight.y)
        close()
    }
    drawPath(path = path, color = Color.White.copy(alpha = 0.5f))
}