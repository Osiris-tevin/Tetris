@file:OptIn(ExperimentalComposeUiApi::class)

package com.tetris.android.ui

import android.view.MotionEvent.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tetris.android.R
import com.tetris.android.logic.Direction
import com.tetris.android.logic.event.GameEvent
import com.tetris.android.logic.util.Clickable
import com.tetris.android.logic.util.Constants.DirectionButtonSize
import com.tetris.android.logic.util.Constants.RotateButtonSize
import com.tetris.android.logic.util.Constants.SettingButtonSize
import com.tetris.android.logic.util.combinedClickable
import com.tetris.android.ui.theme.*
import com.tetris.android.ui.viewModel.GameViewModel
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Preview
@Composable
fun HomeScreen() {
    val gameViewModel = viewModel<GameViewModel>()
    val state = gameViewModel.gameViewState.value

    // 使用LaunchedEffect处理游戏的时间更新
    LaunchedEffect(key1 = Unit) {
        while (isActive) {
            delay(timeMillis = 650L - 55 * (state.level - 1))
            // 向gameViewModel派发一个游戏时间更新事件
            gameViewModel.dispatch(GameEvent.GameTick)
        }
    }

    // 获取当前的生命周期所有者
    val lifecycleOwner = LocalLifecycleOwner.current
    // 使用DisposableEffect处理生命周期事件和资源释放
    DisposableEffect(key1 = Unit) {
        // 创建一个DefaultLifecycleObserver对象, 监听生命周期事件
        val observer = object : DefaultLifecycleObserver {
            // 当生命周期恢复时派发GameEvent.Resume事件
            override fun onResume(owner: LifecycleOwner) {
                gameViewModel.dispatch(GameEvent.Resume)
            }
            // 当生命周期暂停时派发GameEvent.Pause事件
            override fun onPause(owner: LifecycleOwner) {
                gameViewModel.dispatch(GameEvent.Pause)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        // 组件销毁时执行onDispose代码块
        onDispose {
            // 移除生命周期观察者, 避免内存泄漏
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    GameBody(
        combinedClickable(
            onMove = { direction: Direction ->
                if (direction == Direction.Up) gameViewModel.dispatch(GameEvent.Drop)
                else gameViewModel.dispatch(GameEvent.Move(direction))
            },
            onRotate = {
                gameViewModel.dispatch(GameEvent.Rotate)
            },
            onRestart = {
                gameViewModel.dispatch(GameEvent.Reset)
            },
            onPause = {
                if (state.isRunning) {
                    gameViewModel.dispatch(GameEvent.Pause)
                } else {
                    gameViewModel.dispatch(GameEvent.Resume)
                }
            },
            onMute = {
                gameViewModel.dispatch(GameEvent.Mute)
            }
        )
    ) {
        GameScreen(
            modifier = Modifier.fillMaxSize(),
            gameViewModel = gameViewModel
        )
    }
}

@Composable
private fun GameBody(
    clickable: Clickable = combinedClickable(),
    screen: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GameBodyColor)
            .padding(top = 20.dp)
    ) {
        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            // 修饰框
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(330.dp, 400.dp)
                    .padding(top = 20.dp)
                    .background(Color.Black.copy(alpha = 0.8f))
                    .padding(5.dp)
                    .background(GameBodyColor)
            )
            // 游戏名称
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(45.dp)
                    .align(Alignment.TopCenter)
                    .background(GameBodyColor)
            ) {
                Text(
                    text = stringResource(id = R.string.game_label),
                    style = gameLabel,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            // 游戏屏幕
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(360.dp, 380.dp)
                    .padding(start = 50.dp, end = 50.dp, top = 50.dp, bottom = 30.dp)
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
        }
        Spacer(modifier = Modifier.height(20.dp))
        // 游戏按钮
        GameButtons(clickable)
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
private fun GameButtons(
    clickable: Clickable = combinedClickable()
) {
    // 设置按钮
    SettingButtons(clickable)
    Spacer(modifier = Modifier.height(30.dp))
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
                onClick = { clickable.onMove(Direction.Up) },
                autoInvokeWhenPressed = false,
                size = DirectionButtonSize
            ) {
                GameButtonText(modifier = it, text = stringResource(id = R.string.button_up))
            }
            // 按钮: 左
            GameButton(
                modifier = Modifier.align(Alignment.CenterStart),
                onClick = { clickable.onMove(Direction.Left) },
                autoInvokeWhenPressed = false,
                size = DirectionButtonSize
            ) {
                GameButtonText(modifier = it, text = stringResource(id = R.string.button_left))
            }
            // 按钮: 右
            GameButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = { clickable.onMove(Direction.Right) },
                autoInvokeWhenPressed = false,
                size = DirectionButtonSize
            ) {
                GameButtonText(modifier = it, text = stringResource(id = R.string.button_right))
            }
            // 按钮: 下
            GameButton(
                modifier = Modifier.align(Alignment.BottomCenter),
                onClick = { clickable.onMove(Direction.Down) },
                autoInvokeWhenPressed = false,
                size = DirectionButtonSize
            ) {
                GameButtonText(modifier = it, text = stringResource(id = R.string.button_down))
            }
        }
        // 旋转按钮
        Box (
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            GameButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = { clickable.onRotate() },
                autoInvokeWhenPressed = false,
                size = RotateButtonSize
            ) {
                GameButtonText(modifier = it, text = stringResource(id = R.string.button_rotate))
            }
        }
    }
}

@Composable
private fun SettingButtons(
    clickable: Clickable = combinedClickable()
) {
    Column(modifier = Modifier.padding(horizontal = 40.dp)) {
        Row {
            SettingText(modifier = Modifier.weight(1f), text = stringResource(id = R.string.button_sounds))
            SettingText(modifier = Modifier.weight(1f), text = stringResource(id = R.string.button_pause))
            SettingText(modifier = Modifier.weight(1f), text = stringResource(id = R.string.button_reset))
        }
        Spacer(modifier = Modifier.height(5.dp))
        Row {
            // SOUNDS
            GameButton(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                onClick = { clickable.onMute() },
                size = SettingButtonSize
            ) {}
            // PAUSE
            GameButton(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                onClick = { clickable.onPause() },
                size = SettingButtonSize
            ) {}
            // RESET
            GameButton(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                onClick = { clickable.onRestart() },
                size = SettingButtonSize
            ) {}
        }
    }
}

@Composable
private fun SettingText(modifier: Modifier, text: String) {
    Text(
        text = text,
        color = Color.Black.copy(0.9f),
        fontSize = 12.sp,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

@OptIn(ObsoleteCoroutinesApi::class)
@Composable
private fun GameButton(
    modifier: Modifier = Modifier,
    size: Dp,
    onClick: () -> Unit = {},
    autoInvokeWhenPressed: Boolean = false,     // 在按钮按下时是否自动调用onClick回调
    content: @Composable (Modifier) -> Unit = {}
) {
    val backgroundShape = RoundedCornerShape(size / 2)
    lateinit var ticker: ReceiveChannel<Unit>   // 定义一个延迟初始化的ReceiveChannel

    val coroutineScope = rememberCoroutineScope()       // 创建协程作用域
    val pressedInteraction = remember { mutableStateOf<PressInteraction.Press?>(null) } // 保存按钮的按下交互状态
    val interactionSource = MutableInteractionSource()  // 交互事件源

    Box(
        modifier = modifier
            .shadow(elevation = 5.dp, shape = backgroundShape)
            .size(size = size)
            .clip(backgroundShape)
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
            .indication(interactionSource = interactionSource, indication = rememberRipple())
            .run {
                if (autoInvokeWhenPressed) {    // 如果设置了自动触发点击事件
                    pointerInteropFilter {      // 处理指针(触摸)事件的过滤器
                        when (it.action) {
                            ACTION_DOWN -> {
                                coroutineScope.launch {
                                    // 如果我们没有正确停止/取消操作, 则删除任何旧的交互
                                    pressedInteraction.value?.let { oldValue ->
                                        val interaction = PressInteraction.Cancel(oldValue)
                                        interactionSource.emit(interaction)
                                        pressedInteraction.value = null
                                    }
                                    val interaction = PressInteraction.Press(Offset(50f, 50f))
                                    interactionSource.emit(interaction)
                                    pressedInteraction.value = interaction
                                }

                                ticker = ticker(initialDelayMillis = 300, delayMillis = 60)
                                coroutineScope.launch {
                                    ticker
                                        .receiveAsFlow()
                                        .collect { onClick() }
                                }
                            }
                            ACTION_CANCEL, ACTION_UP -> {
                                coroutineScope.launch {
                                    pressedInteraction.value?.let { oldValue ->
                                        val interaction = PressInteraction.Cancel(oldValue)
                                        interactionSource.emit(interaction)
                                        pressedInteraction.value = null
                                    }
                                }

                                ticker.cancel()
                                if (it.action == ACTION_UP) {
                                    onClick()
                                }
                            }
                            else -> {
                                if (it.action != ACTION_MOVE) {
                                    ticker.cancel()
                                }
                                return@pointerInteropFilter false
                            }
                        }
                        true
                    }
                } else {
                    clickable { onClick() }
                }
            }
    ) {
        content(Modifier.align(Alignment.Center))
    }
}

@Composable
private fun GameButtonText(modifier: Modifier, text: String) {
    Text(
        text = text,
        style = button,
        color = Color.White.copy(0.9f),
        fontSize = 18.sp,
        modifier = modifier
    )
}

@Composable
private fun GameVersionInfo() {
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
private fun DrawScope.drawScreenBorder(
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