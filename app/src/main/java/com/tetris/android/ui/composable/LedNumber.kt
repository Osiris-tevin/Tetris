package com.tetris.android.ui.composable

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tetris.android.ui.theme.BrickMatrix
import com.tetris.android.ui.theme.BrickSprite
import com.tetris.android.ui.theme.LedFontFamily
import com.tetris.android.ui.theme.ledNumber
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@SuppressLint("SimpleDateFormat")
@Composable
fun LedClock(modifier: Modifier = Modifier) {
    // 创建一个无限循环动画, 值在0f到1f之间变化
    val animateValue by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // 存储时钟的小时和分钟
    var clock by remember { mutableStateOf(0 to 0) }

    // 当animateValue的整数部分发生变化时, 更新时钟的值
    DisposableEffect(key1 = animateValue.roundToInt()) {
        val dateFormat: DateFormat = SimpleDateFormat("H,m")
        // 获取当前的小时和分钟, 并存储到clock变量中
        val (currentHour, currentMinute) = dateFormat.format(Date()).split(",")
        clock = currentHour.toInt() to currentMinute.toInt()
        onDispose {  }  // 组件被丢弃时执行的操作
    }

    // 呈现一个水平排列的时钟数字
    Row(modifier = modifier) {
        // 显示小时的LED数字
        LedNumber(number = clock.first, digits = 2, fillZero = true)
        // 显示冒号
        Box(
            modifier = Modifier
                .width(6.dp)
                .padding(end = 1.dp)
        ) {
            LedColonText(color = BrickMatrix)
            // 根据animateValue的值判断是否显示另一种颜色的冒号
            if (animateValue.roundToInt() == 1) {
                LedColonText(color = BrickSprite)
            }
        }
        // 显示分钟的LED数字
        LedNumber(number = clock.second, digits = 2, fillZero = true)
    }
}

/**
 * 显示Led数字
 * @param modifier Modifier实例对象
 * @param number 待显示的数字
 * @param digits 数字的位数
 * @param fillZero 是否填充零位, 默认为false
 */
@Composable
fun LedNumber(
    modifier: Modifier = Modifier,
    number: Int,
    digits: Int,
    fillZero: Boolean = false
) {
    Box(modifier = modifier) {
        Row(modifier = Modifier.align(Alignment.CenterEnd)) {
            repeat(digits) {
                LedNumberText(textColor = BrickMatrix)
            }
        }
        Row(modifier = Modifier.align(Alignment.CenterEnd)) {
            val formattedNumber = if (fillZero) String.format("%0${digits}d", number) else number.toString()
            formattedNumber.iterator().forEach { text ->
                LedNumberText(text = text.toString(), textColor = BrickSprite)
            }
        }
    }
}

@Composable
private fun LedNumberText(
    text: String = "8",
    textColor: Color
) {
    Text(
        text = text,
        style = ledNumber,
        color = textColor,
        textAlign = TextAlign.End,
        modifier = Modifier.width(8.dp)
    )
}

@Composable
private fun LedColonText(color: Color) {
    Text(
        text = ":",
        fontSize = 16.sp,
        fontFamily = LedFontFamily,
        color = color,
        textAlign = TextAlign.End,
        modifier = Modifier.fillMaxWidth()
    )
}